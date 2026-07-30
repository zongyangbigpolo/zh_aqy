<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工程项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择工程项目" style="width: 100%">
          <el-option v-for="(item,index ) in projectOptions" :key='index' :value="item.id"
                     :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备名称" prop="eqmtName">
        <el-input
          v-model="queryParams.eqmtName"
          placeholder="请输入设备名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备编码" prop="eqmtCode">
        <el-input
          v-model="queryParams.eqmtCode"
          placeholder="请输入设备编码"
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
          v-hasPermi="['aqy:alarmEquipment:add']"
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
          v-hasPermi="['aqy:alarmEquipment:edit']"
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
          v-hasPermi="['aqy:alarmEquipment:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['aqy:alarmEquipment:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="alarmEquipmentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="工程项目" align="center" prop="projectId">
        <template slot-scope="scope">
          <span>{{ functionProjectName(scope.row.projectId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="设备名称" align="center" prop="eqmtName" />
      <el-table-column label="设备编码" align="center" prop="eqmtCode" />
      <el-table-column label="在线状态" align="center" prop="onlineStatus" />
      <el-table-column label="报警时间(秒)" align="center" prop="alarmTime" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['aqy:alarmEquipment:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['aqy:alarmEquipment:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-unfold"
            @click="handleProfile(scope.row)"
          >详情</el-button>
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

    <!-- 添加或修改声光报警设备对话框 -->
    <el-dialog :title="title" :visible.sync="open" :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="工程项目" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择工程项目" style="width: 100%">
            <el-option v-for="(item,index ) in projectOptions" :key='index' :value="item.id"
                       :label="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备名称" prop="eqmtName">
          <el-input v-model="form.eqmtName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备编码" prop="eqmtCode">
          <el-input v-model="form.eqmtCode" placeholder="请输入设备编码" />
        </el-form-item>
        <el-form-item label="报警时间(秒)" prop="alarmTime">
          <el-input-number
            v-model="form.alarmTime"
            :min="60"
            placeholder="请输入报警时间(秒)"
            style="width: 100%"
          />
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
import { listAlarmEquipment, getAlarmEquipment, delAlarmEquipment, addAlarmEquipment, updateAlarmEquipment } from "@/api/aqy/alarmEquipment";
import {listProject} from "@/api/aqy/project";
import equipmentDetail from "./detail";

export default {
  name: "AlarmEquipment",
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
      // 声光报警设备表格数据
      alarmEquipmentList: [],
      projectOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        projectId: null,
        eqmtName: null,
        eqmtCode: null,
        onlineStatus: null,
        createUid: null,
        isDelete: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        projectId: [
          { required: true, message: "请选择工程项目", trigger: "blur" }
        ],
        eqmtName: [
          { required: true, message: "请输入设备名称", trigger: "blur" }
        ],
        eqmtCode: [
          { required: true, message: "请输入设备编码", trigger: "blur" }
        ],
        alarmTime: [
          { required: true, message: "请输入报警时间", trigger: "blur" },
          { type: 'number', message: "报警时间必须为数字", trigger: "blur" },
        ]
      },
      titleProfile: "",
      openProfile: false,
    };
  },
  created() {
    this.getList();
    this.getProjects();
  },
  methods: {
    /** 获取工程项目列表 */
    getProjects() {
      listProject().then(response => {
        this.projectOptions = response.rows;
        if (response.rows)
          this.queryParams.projectId = response.rows[0].id
        else
          this.queryParams.projectId = null;

      });
    },
    functionProjectName(projectId) {
      const project = this.projectOptions.find(item => item.id === projectId)
      return project ? project.name : ''
    },
    /** 查询声光报警设备列表 */
    getList() {
      this.loading = true;
      listAlarmEquipment(this.queryParams).then(response => {
        this.alarmEquipmentList = response.rows;
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
        eqmtName: null,
        eqmtCode: null,
        alarmTime: 60, // 默认60秒
        onlineStatus: null,
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
      this.title = "添加声光报警设备";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAlarmEquipment(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改声光报警设备";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAlarmEquipment(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAlarmEquipment(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除声光报警设备编号为"' + ids + '"的数据项？').then(function() {
        return delAlarmEquipment(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('aqy/alarmEquipment/export', {
        ...this.queryParams
      }, `alarmEquipment_${new Date().getTime()}.xlsx`)
    },
    /**
     * 查看详情
     * @param row
     */
    handleProfile(row){
      this.titleProfile = "【" + row.eqmtName + "】声光报警设备详情";
      this.openProfile = true;
      this.$nextTick(() => {
        this.$refs.equipmentDetail.init(row)
      })
    },
  }
};
</script>
