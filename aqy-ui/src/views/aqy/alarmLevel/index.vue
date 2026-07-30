<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="设备类型" prop="eqmtType">
        <el-select v-model="queryParams.eqmtType" placeholder="请选择设备类型" clearable>
          <el-option v-for="( item,index ) in eqmtTypeOptions" :key='index' :value="item.id"
                     :label="item.eqmtTypeName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="报警等级" prop="alarmLevel">
        <el-input
          v-model="queryParams.alarmLevel"
          placeholder="请输入报警等级"
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
      <div class="text-danger">* 报警严重程度：一级 > 二级 > 三级</div>
    </el-row>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['aqy:alarmLevel:add']"
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
          v-hasPermi="['aqy:alarmLevel:edit']"
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
          v-hasPermi="['aqy:alarmLevel:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['aqy:alarmLevel:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="alarmLevelList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="设备类型" align="center" prop="eqmtType">
        <template slot-scope="scope">
          <span>{{ functionAqyEquipmentType(scope.row.eqmtType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报警等级" align="center" prop="alarmLevel">
        <template slot-scope="scope">
          <span>{{scope.row.alarmLevel === 1 ? '一级' : scope.row.alarmLevel === 2 ? '二级' : scope.row.alarmLevel === 3 ? '三级' : '' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="阈值" align="center" prop="accumulativeThresholdValue" />
      <el-table-column label="信息推送次数" align="center" prop="alarmCount" />
      <el-table-column label="采集任务频率" align="center" prop="cronExpress" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['aqy:alarmLevel:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['aqy:alarmLevel:remove']"
          >删除</el-button>
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-s-unfold"-->
<!--            @click="handleProfile(scope.row)"-->
<!--          >详情</el-button>-->
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

    <!-- 添加或修改报警等级对话框 -->
    <el-dialog :title="title" :visible.sync="open" :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="设备类型" prop="eqmtType">
          <el-select v-model="form.eqmtType" placeholder="请选择设备类型" style="width: 100%;">
            <el-option v-for="( item,index ) in eqmtTypeOptions" :key='index' :value="item.id"
                       :label="item.eqmtTypeName"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="报警等级" prop="alarmLevel">
          <el-select v-model="form.alarmLevel" placeholder="请选择报警等级" style="width: 100%">
            <el-option v-for="(item,index ) in levelOptions" :key='index' :value="item.id"
                       :label="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="阈值" prop="accumulativeThresholdValue">
          <el-input v-model="form.accumulativeThresholdValue" placeholder="请输入累计变化阈值" type="number"/>
        </el-form-item>
        <el-form-item label="信息推送次数" prop="alarmCount">
          <el-input v-model="form.alarmCount" placeholder="请输入信息推送次数" />
        </el-form-item>
        <el-form-item label="采集任务频率" prop="cronExpress">
          <el-input v-model="form.cronExpress" placeholder="请输入cron执行表达式">
            <template slot="append">
              <el-button type="primary" @click="handleShowCron">
                生成表达式
                <i class="el-icon-time el-icon--right"></i>
              </el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="Cron表达式生成器" :visible.sync="openCron" append-to-body destroy-on-close class="scrollbar">
      <crontab @hide="openCron=false" @fill="crontabFill" :expression="expression"></crontab>
    </el-dialog>
  </div>
</template>

<script>
import { listAlarmLevel, getAlarmLevel, delAlarmLevel, addAlarmLevel, updateAlarmLevel } from "@/api/aqy/alarmLevel";
import {listAqyEquipmentType} from "@/api/aqy/aqyEquipmentType";
import Crontab from "@/components/Crontab/index.vue";

export default {
  name: "AlarmLevel",
  components: {Crontab},
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
      // 报警等级表格数据
      alarmLevelList: [],

      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        eqmtType: null,
        alarmLevel: null,
        instantThresholdValue: null,
        accumulativeThresholdValue: null,
        alarmColor: null,
        alarmTemplate: null,
        alarmCount: null,
        createUid: null,
        isDelete: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        eqmtType: [
          { required: true, message: "请选择设备类型", trigger: "blur" }
        ],
        alarmLevel: [
          { required: true, message: "请输入报警等级", trigger: "blur" }
        ],
      },
      eqmtTypeOptions: [],
      titleProfile: "",
      openProfile: false,
      frequencyDetails: {},
      // 是否显示Cron表达式弹出层
      openCron: false,
      // 传入的表达式
      expression: "",
      levelOptions: [
        {id: 3, name: '三级'},
        {id: 2, name: '二级'},
        {id: 1, name: '一级'},
      ],
    };
  },
  created() {
    this.getEquipmentTypes();
  },
  methods: {
    getEquipmentTypes() {
      listAqyEquipmentType({}).then(response => {
        this.eqmtTypeOptions = response.rows;
        this.getList();
      });
    },
    functionAqyEquipmentType(eqmtTypeId) {
      const eqmtType = this.eqmtTypeOptions.find(item => item.id === eqmtTypeId)
      return eqmtType ? eqmtType.eqmtTypeName : ''
    },
    /** 查询报警等级列表 */
    getList() {
      this.loading = true;
      listAlarmLevel(this.queryParams).then(response => {
        this.alarmLevelList = response.rows;
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
        eqmtType: null,
        alarmLevel: null,
        instantThresholdValue: null,
        accumulativeThresholdValue: null,
        upperLimit: null,
        lowerLimit: null,
        alarmColor: null,
        alarmTemplate: null,
        alarmCount: null,
        createTime: null,
        createUid: null,
        isDelete: null,
        cronExpress: null,
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
      this.title = "添加报警等级";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAlarmLevel(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改报警等级";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAlarmLevel(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAlarmLevel(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除报警等级编号为"' + ids + '"的数据项？').then(function() {
        return delAlarmLevel(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('aqy/alarmLevel/export', {
        ...this.queryParams
      }, `alarmLevel_${new Date().getTime()}.xlsx`)
    },
    // 查看详情
    handleProfile(row){
      this.titleProfile = "【" + row.alarmLevel + "】详情";
      this.frequencyDetails = row;
      this.openProfile = true;
    },
    /** cron表达式按钮操作 */
    handleShowCron() {
      this.expression = this.form.cronExpress;
      this.openCron = true;
    },
    /** 确定后回传值 */
    crontabFill(value) {
      this.form.cronExpress = value;
    },
  }
};
</script>
