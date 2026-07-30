<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="设备类型名称" prop="eqmtTypeName">
        <el-input
          v-model="queryParams.eqmtTypeName"
          placeholder="请输入设备类型名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['aqy:aqyEquipmentType:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['aqy:aqyEquipmentType:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['aqy:aqyEquipmentType:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['aqy:aqyEquipmentType:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="aqyEquipmentTypeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="设备类型名称" align="center" prop="eqmtTypeName" />
      <el-table-column label="正常上传间隔(s)" align="center" prop="uploadIntv" />
      <el-table-column label="加报上传间隔(s)" align="center" prop="plusIntv" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['aqy:aqyEquipmentType:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['aqy:aqyEquipmentType:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改设备类型对话框 -->
    <el-dialog :title="title" :visible.sync="open"  :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="设备类型名称" prop="eqmtTypeName">
          <el-input v-model="form.eqmtTypeName" placeholder="请输入设备类型名称" />
        </el-form-item>
        <el-form-item label="正常上传间隔(s)" prop="uploadIntv">
          <el-input v-model="form.uploadIntv" placeholder="请输入正常上传间隔(s)" type="number" />
        </el-form-item>
        <el-form-item label="加报上传间隔(s)" prop="plusIntv">
          <el-input v-model="form.plusIntv" placeholder="请输入加报上传间隔(s)" type="number" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAqyEquipmentType, getAqyEquipmentType, delAqyEquipmentType, addAqyEquipmentType, updateAqyEquipmentType } from "@/api/aqy/aqyEquipmentType";

export default {
  name: "AqyEquipmentType",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 设备类型表格数据
      aqyEquipmentTypeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        eqmtTypeName: null,
        uploadIntv: null,
        plusIntv: null,
        createUid: null,
        isDelete: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        eqmtTypeName: [ { required: true, message: "请输入设备类型名称", trigger: "blur" } ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询设备类型列表 */
    getList() {
      this.loading = true;
      listAqyEquipmentType(this.queryParams).then(response => {
        this.aqyEquipmentTypeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        eqmtTypeName: null,
        uploadIntv: null,
        plusIntv: null,
        createTime: null,
        createUid: null,
        isDelete: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加设备类型";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAqyEquipmentType(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改设备类型";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAqyEquipmentType(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAqyEquipmentType(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除设备类型编号为"' + ids + '"的数据项？').then(function() {
        return delAqyEquipmentType(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('aqy/aqyEquipmentType/export', {
        ...this.queryParams
      }, `aqyEquipmentType_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
