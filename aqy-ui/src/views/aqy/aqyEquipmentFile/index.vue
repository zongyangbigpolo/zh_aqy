<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工程项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择工程项目">
          <el-option v-for="( item,index ) in projectOptions" :key='index' :value="item.id"
                     :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="采集设备" prop="eqmtId">
        <el-select v-model="queryParams.eqmtId" placeholder="请选择采集设备" style="width: 100%">
          <el-option v-for="( item,index ) in aqyEquipmentList" :key='index' :value="item.id"
                     :label="item.eqmtName"></el-option>
        </el-select>
        <!--        <el-input-->
        <!--          v-model="queryParams.eqmtId"-->
        <!--          placeholder="请输入采集设备"-->
        <!--          clearable-->
        <!--          @keyup.enter.native="handleQuery"-->
        <!--        />-->
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
          v-hasPermi="['aqy:aqyEquipmentFile:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['aqy:aqyEquipmentFile:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['aqy:aqyEquipmentFile:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['aqy:aqyEquipmentFile:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="aqyEquipmentFileList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="工程项目" align="center" prop="projectId">
        <template slot-scope="scope">
          <span>{{ functionProjectName(scope.row.projectId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="采集设备" align="center" prop="eqmtId">
        <template slot-scope="scope">
          <span>{{ functionEquipmentName(scope.row.eqmtId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="文件名称" align="center" prop="fileName"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['aqy:aqyEquipmentFile:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['aqy:aqyEquipmentFile:remove']"
          >删除
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-download"
            @click="handleDownload(scope.row)"
            v-hasPermi="['aqy:aqyEquipmentFile:remove']"
          >下载
          </el-button>
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

    <!-- 添加或修改采集设备的证书文件对话框 -->
    <el-dialog :title="title" :visible.sync="open" :close-on-click-modal="false" class="showAll_dialog" append-to-body
               v-el-drag-dialog>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="工程项目" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择工程项目" style="width: 100%"
                     @change="handleChangeProject">
            <el-option v-for="(item,index ) in projectOptions" :key='index' :value="item.id"
                       :label="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="采集设备" prop="eqmtId">
          <el-select v-model="form.eqmtId" placeholder="请选择采集设备" style="width: 100%">
            <el-option v-for="( item,index ) in aqyEquipmentList" :key='index' :value="item.id"
                       :label="item.eqmtName"></el-option>
          </el-select>
          <!--          <el-input v-model="form.eqmtId" placeholder="请输入采集设备ID" />-->
        </el-form-item>
        <el-form-item label="文件名称" prop="fileName">
          <el-input v-model="form.fileName" placeholder="请输入文件名称"/>
        </el-form-item>
        <el-form-item label="上传附件" prop="fileUrl">
          <FileUpload v-model="form.fileUrl"/>
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
import {
  listAqyEquipmentFile,
  getAqyEquipmentFile,
  delAqyEquipmentFile,
  addAqyEquipmentFile,
  updateAqyEquipmentFile
} from "@/api/aqy/aqyEquipmentFile";
import {listProject} from "@/api/aqy/project";
import {listAqyEquipment} from "@/api/aqy/aqyEquipment";
import User from "@/views/system/user";

export default {
  name: "AqyEquipmentFile",
  components: {User},
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
      // 采集设备的证书文件表格数据
      aqyEquipmentFileList: [],
      projectOptions: [],
      aqyEquipmentList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        projectId: null,
        eqmtId: null,
        fileUrl: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        projectId: [
          {required: true, message: "请选择工程项目", trigger: "blur"}
        ],
        eqmtId: [
          {required: true, message: "请选择采集设备", trigger: "blur"}
        ],
        fileName: [
          {required: true, message: "请输入文件名称", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getProjects();
  },
  methods: {
    functionEquipmentName(eqmtId) {
      const equipment = this.aqyEquipmentList.find(item => item.id === eqmtId)
      return equipment ? equipment.eqmtName : ''
    },
    functionProjectName(projectId) {
      const project = this.projectOptions.find(item => item.id === projectId)
      return project ? project.name : ''
    },
    getProjects() {
      listProject().then(response => {
        this.projectOptions = response.rows;
        if (response.rows)
          this.queryParams.projectId = response.rows[0].id
        else
          this.queryParams.projectId = null;

        this.getAqyEquipment(this.queryParams.projectId)
      });
    },
    /** 查询采集设备的证书文件列表 */
    getList() {
      this.loading = true;
      listAqyEquipmentFile(this.queryParams).then(response => {
        this.aqyEquipmentFileList = response.rows;
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
        projectId: null,
        eqmtId: null,
        fileUrl: null,
        createTime: null
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
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加采集设备的证书文件";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAqyEquipmentFile(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改采集设备的证书文件";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAqyEquipmentFile(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAqyEquipmentFile(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除采集设备的证书文件编号为"' + ids + '"的数据项？').then(function () {
        return delAqyEquipmentFile(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('aqy/aqyEquipmentFile/export', {
        ...this.queryParams
      }, `aqyEquipmentFile_${new Date().getTime()}.xlsx`)
    },
    handleDownload(row) {
      window.open(process.env.VUE_APP_BASE_API + row.fileUrl, '_blank');
    },
    handleChangeProject() {
      this.getAqyEquipment(this.form.projectId);
    },
    /** 查询数据采集设备列表 */
    getAqyEquipment(projectId) {
      listAqyEquipment({projectId: projectId}).then(response => {
        this.aqyEquipmentList = response.rows;
      });
    },
  }
};
</script>
