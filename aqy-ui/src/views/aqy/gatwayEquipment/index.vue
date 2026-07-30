<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="工程项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择工程项目">
          <el-option v-for="( item,index ) in projectOptions" :key='index' :value="item.id"
                     :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="智能网关名称" prop="gatwayName">
        <el-input
          v-model="queryParams.gatwayName"
          placeholder="请输入智能网关名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="智能网关编码" prop="gatwayCode">
        <el-input
          v-model="queryParams.gatwayCode"
          placeholder="请输入智能网关编码"
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
          v-hasPermi="['aqy:gatwayEquipment:add']"
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
          v-hasPermi="['aqy:gatwayEquipment:edit']"
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
          v-hasPermi="['aqy:gatwayEquipment:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['aqy:gatwayEquipment:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-download"
          size="mini"
          :disabled="multiple"
          @click="handleBatchDownConfig"
          v-hasPermi="['system:equipment:edit']"
        >一键下发网关</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-download"
          size="mini"
          :disabled="multiple"
          @click="handleBatchSetSampleParam"
          v-hasPermi="['system:equipment:edit']"
        >一键下发采集参数</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-download"
          size="mini"
          :disabled="multiple"
          @click="handleBatchSetThresholdValue"
          v-hasPermi="['system:equipment:edit']"
        >一键下发阈值</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="gatwayEquipmentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="工程项目" align="center" prop="projectId">
        <template slot-scope="scope">
          <span>{{ functionProjectName(scope.row.projectId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="智能网关名称" align="center" prop="gatwayName" />
      <el-table-column label="智能网关编码" align="center" prop="gatwayCode" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['aqy:gatwayEquipment:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['aqy:gatwayEquipment:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-unfold"
            @click="handleProfile(scope.row)"
          >详情</el-button>
          <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)" v-hasPermi="['system:user:resetPwd', 'system:user:edit']">
            <el-button size="mini" type="text" icon="el-icon-d-arrow-right">更多</el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="handleDownConfig" icon="el-icon-download"
                                v-hasPermi="['system:user:resetPwd']">下发网关</el-dropdown-item>
              <el-dropdown-item command="handleSetSampleParam" icon="el-icon-download"
                                v-hasPermi="['system:user:edit']">下发采集参数</el-dropdown-item>
              <el-dropdown-item command="handleSetThresholdParam" icon="el-icon-download"
                                v-hasPermi="['system:user:edit']">下发阈值</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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

    <!-- 添加或修改智能网关设备对话框 -->
    <el-dialog :title="title" :visible.sync="open"  :close-on-press-escape="false"
               :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="工程项目" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择工程项目" style="width: 100%">
            <el-option v-for="(item,index ) in projectOptions" :key='index' :value="item.id"
                       :label="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="智能网关名称" prop="gatwayName">
          <el-input v-model="form.gatwayName" placeholder="请输入智能网关名称" />
        </el-form-item>
        <el-form-item label="智能网关编码" prop="gatwayCode">
          <el-input v-model="form.gatwayCode" placeholder="请输入智能网关编码" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情页面 -->
    <el-dialog :title="titleProfile" :visible.sync="openProfile" width="80%"   :close-on-press-escape="false"
               :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog>
      <equipment-detail ref="equipmentDetail"></equipment-detail>
    </el-dialog>
  </div>
</template>

<script>
import { listGatwayEquipment, getGatwayEquipment, delGatwayEquipment, addGatwayEquipment, updateGatwayEquipment } from "@/api/aqy/gatwayEquipment";
import {
  batchDownConfig,
  downConfig,
  batchSetSampleParam,
  setSampleParam,
  batchSetThresholdValue, setThresholdValue
} from "@/api/aqy/cmdMqtt";
import {listProject} from "@/api/aqy/project";
import {listAqyEquipment} from "@/api/aqy/aqyEquipment";
import {listAqyEquipmentType} from "@/api/aqy/aqyEquipmentType";
import equipmentDetail from "./detail";

export default {
  name: "GatwayEquipment",
  components: {
    "equipment-detail": equipmentDetail
  },
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
      // 智能网关设备表格数据
      gatwayEquipmentList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        projectId: null,
        gatwayName: null,
        gatwayCode: null,
        createUid: null,
        isDelete: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        projectId: [
          { required: true, message: "请选择工程项目", trigger: "blur" } ],
        gatwayName: [
          { required: true, message: "请输入智能网关名称", trigger: "blur" } ],
        gatwayCode: [
          { required: true, message: "请输入智能网关编码", trigger: "blur" } ]
      },
      projectOptions: [],
      titleProfile: "",
      openProfile: false,
      gatwayDetails: {},
      gatwayEquipments: [],
    };
  },
  created() {
    this.getProjects();
  },
  methods: {
    getProjects() {
      listProject().then(response => {
        this.projectOptions = response.rows;
        if (response.rows)
          this.queryParams.projectId = response.rows[0].id
        else
          this.queryParams.projectId = null;
        this.getList();
      });
    },
    functionProjectName(projectId) {
      const project = this.projectOptions.find(item => item.id === projectId)
      return project ? project.name : ''
    },
    /** 查询智能网关设备列表 */
    getList() {
      this.loading = true;
      listGatwayEquipment(this.queryParams).then(response => {
        this.gatwayEquipmentList = response.rows;
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
        gatwayName: null,
        gatwayCode: null,
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
      this.title = "添加智能网关设备";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getGatwayEquipment(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改智能网关设备";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateGatwayEquipment(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addGatwayEquipment(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除智能网关设备编号为"' + ids + '"的数据项？').then(function() {
        return delGatwayEquipment(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('aqy/gatwayEquipment/export', {
        ...this.queryParams
      }, `gatwayEquipment_${new Date().getTime()}.xlsx`)
    },
    /**
     * 查看详情
     * @param row
     */
    handleProfile(row){
      this.titleProfile = "【" + row.gatwayName + "】网关详情";
      this.openProfile = true;
      this.$nextTick(() => {
        this.$refs.equipmentDetail.init(row)
      })
    },
    // 更多操作触发
    handleCommand(command, row) {
      switch (command) {
        case "handleDownConfig":
          this.handleDownConfig(row);
          break;
        case "handleSetSampleParam":
          this.handleSetSampleParam(row);
          break;
        case "handleSetThresholdParam":
          this.handleSetThresholdParam(row);
          break;
        default:
          break;
      }
    },
    /**
     * 批量下发网关
     */
    handleBatchDownConfig(){
      const ids = this.ids;
      this.$modal.confirm('是否确认下发该网关下的传感器？').then(function() {
        return batchDownConfig(ids);
      }).then(() => {
        this.$modal.msgSuccess("下发成功");
      }).catch(() => {});
    },
    /**
     * 下发网关
     */
    handleDownConfig(row){
      this.$modal.confirm('是否确认下发该网关下的传感器？').then(function() {
        return downConfig(row);
      }).then(() => {
        this.$modal.msgSuccess("下发成功");
      }).catch(() => {});
    },
    /**
     * 批量下发网关采集参数
     */
    handleBatchSetSampleParam(){
      const ids = this.ids;
      this.$modal.confirm('是否确认下发采集参数？').then(function() {
        return batchSetSampleParam(ids);
      }).then(() => {
        this.$modal.msgSuccess("下发成功");
      }).catch(() => {});
    },
    /**
     * 下发网关采集参数
     * @param row
     */
    handleSetSampleParam(row){
      this.$modal.confirm('是否确认下发采集参数？').then(function() {
        return setSampleParam(row);
      }).then(() => {
        this.$modal.msgSuccess("下发成功");
      }).catch(() => {});
    },
    /**
     * 批量下发阈值
     */
    handleBatchSetThresholdValue(){
      const ids = this.ids;
      this.$modal.confirm('是否确认下发阈值？').then(function() {
        return batchSetThresholdValue(ids);
      }).then(() => {
        this.$modal.msgSuccess("下发成功");
      }).catch(() => {});
    },
    /**
     * 下发阈值
     * @param row
     */
    handleSetThresholdParam(row){
      this.$modal.confirm('是否确认下发阈值？').then(function() {
        return setThresholdValue(row);
      }).then(() => {
        this.$modal.msgSuccess("下发成功");
      }).catch(() => {});
    }
  }
};
</script>
