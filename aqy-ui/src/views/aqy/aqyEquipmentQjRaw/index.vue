<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="设备ID" prop="eqmtId">
        <el-input
          v-model="queryParams.eqmtId"
          placeholder="请输入设备ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备编号" prop="eqmtCode">
        <el-input
          v-model="queryParams.eqmtCode"
          placeholder="请输入设备编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备名称" prop="eqmtName">
        <el-input
          v-model="queryParams.eqmtName"
          placeholder="请输入设备名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上传时间戳" prop="catchTime">
        <el-input
          v-model="queryParams.catchTime"
          placeholder="请输入上传时间戳"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="X角度(°)" prop="xValue">
        <el-input
          v-model="queryParams.xValue"
          placeholder="请输入X角度(°)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Y角度(°)" prop="yValue">
        <el-input
          v-model="queryParams.yValue"
          placeholder="请输入Y角度(°)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="温度(℃)" prop="tempValue">
        <el-input
          v-model="queryParams.tempValue"
          placeholder="请输入温度(℃)"
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
          v-hasPermi="['aqy:aqyEquipmentQjRaw:add']"
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
          v-hasPermi="['aqy:aqyEquipmentQjRaw:edit']"
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
          v-hasPermi="['aqy:aqyEquipmentQjRaw:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['aqy:aqyEquipmentQjRaw:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="aqyEquipmentQjRawList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="设备ID" align="center" prop="eqmtId" />
      <el-table-column label="设备编号" align="center" prop="eqmtCode" />
      <el-table-column label="设备名称" align="center" prop="eqmtName" />
      <el-table-column label="上传时间戳" align="center" prop="catchTime" />
      <el-table-column label="X角度(°)" align="center" prop="xValue" />
      <el-table-column label="Y角度(°)" align="center" prop="yValue" />
      <el-table-column label="Z角度(°)" align="center" prop="zValue" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['aqy:aqyEquipmentQjRaw:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['aqy:aqyEquipmentQjRaw:remove']"
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

    <!-- 添加或修改倾角监测设备上传数据记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="设备ID" prop="eqmtId">
          <el-input v-model="form.eqmtId" placeholder="请输入设备ID" />
        </el-form-item>
        <el-form-item label="设备编号" prop="eqmtCode">
          <el-input v-model="form.eqmtCode" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="设备名称" prop="eqmtName">
          <el-input v-model="form.eqmtName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="上传时间戳" prop="catchTime">
          <el-input v-model="form.catchTime" placeholder="请输入上传时间戳" />
        </el-form-item>
        <el-form-item label="X角度(°)" prop="xValue">
          <el-input v-model="form.xValue" placeholder="请输入X角度(°)" />
        </el-form-item>
        <el-form-item label="Y角度(°)" prop="yValue">
          <el-input v-model="form.yValue" placeholder="请输入Y角度(°)" />
        </el-form-item>
        <el-form-item label="温度(℃)" prop="tempValue">
          <el-input v-model="form.tempValue" placeholder="请输入温度(℃)" />
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
import { listAqyEquipmentQjRaw, getAqyEquipmentQjRaw, delAqyEquipmentQjRaw, addAqyEquipmentQjRaw, updateAqyEquipmentQjRaw } from "@/api/aqy/aqyEquipmentQjRaw";

export default {
  name: "AqyEquipmentQjRaw",
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
      // 倾角监测设备上传数据记录表格数据
      aqyEquipmentQjRawList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        eqmtId: null,
        eqmtCode: null,
        eqmtName: null,
        catchTime: null,
        xValue: null,
        yValue: null,
        tempValue: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询倾角监测设备上传数据记录列表 */
    getList() {
      this.loading = true;
      listAqyEquipmentQjRaw(this.queryParams).then(response => {
        this.aqyEquipmentQjRawList = response.rows;
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
        eqmtId: null,
        eqmtCode: null,
        eqmtName: null,
        catchTime: null,
        xValue: null,
        yValue: null,
        tempValue: null
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
      this.title = "添加倾角监测设备上传数据记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAqyEquipmentQjRaw(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改倾角监测设备上传数据记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAqyEquipmentQjRaw(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAqyEquipmentQjRaw(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除倾角监测设备上传数据记录编号为"' + ids + '"的数据项？').then(function() {
        return delAqyEquipmentQjRaw(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('aqy/aqyEquipmentQjRaw/export', {
        ...this.queryParams
      }, `aqyEquipmentQjRaw_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
