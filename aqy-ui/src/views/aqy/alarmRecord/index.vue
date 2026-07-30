<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工程项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择工程项目">
          <el-option v-for="( item,index ) in projectOptions" :key='index' :value="item.id"
                     :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备类型" prop="eqmtTypeId">
        <el-select v-model="queryParams.eqmtTypeId" placeholder="请选择设备类型" @change="handleEmqtTypeChange">
          <el-option v-for="(item, index) in eqmtTypeOptions" :key='index' :value="item.id" :label="item.eqmtTypeName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备" prop="qmtId">
        <el-select v-model="queryParams.qmtId" placeholder="请选择设备" clearable>
          <el-option v-for="( item,index ) in eqmtOptions" :key='index' :value="item.id"
                     :label="item.eqmtName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="报警等级" prop="alarmId">
        <el-select v-model="queryParams.alarmId" placeholder="请选择报警等级" clearable>
          <el-option v-for="( item,index ) in alarmLevelOptions" :key='index' :value="item.id"
                     :label="item.alarmLevel"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="报警时间" prop="startTime">
        <el-date-picker clearable
          v-model="queryParams.startTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择报警时间">
        </el-date-picker>
        -
        <el-date-picker clearable
                        v-model="queryParams.endTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择报警时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['aqy:alarmRecord:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="alarmRecordList" :max-height="tableHeight" @selection-change="handleSelectionChange">
      <el-table-column type="index" width="60" align="center" label="序号"/>
      <el-table-column label="设备名称" align="center" prop="eqmtName" sortable/>
      <el-table-column label="报警等级" align="center" prop="alarmLevel" sortable>
        <template slot-scope="scope">
          <div class="alarm-level-tag" :class="getAlarmLevelClass(scope.row.alarmLevel)">
            {{ getAlarmLevelText(scope.row.alarmLevel) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="报警时间" align="center" prop="recordTime" width="180" sortable>
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.recordTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="位移累计变化量(mm)" align="center" prop="accumulativeValue1" v-if="searchType === 'WY'"/>
      <el-table-column label="裂缝累计变化量(mm)" align="center" prop="accumulativeValue1" v-if="searchType === 'LF'"/>
      <el-table-column label="X角度累计变化量(°)" align="center" prop="accumulativeValue1" v-if="searchType === 'QJ'"/>
      <el-table-column label="Y角度累计变化量(°)" align="center" prop="accumulativeValue2" v-if="searchType === 'QJ'"/>
      <el-table-column label="Z角度累计变化量(°)" align="center" prop="accumulativeValue3" v-if="searchType === 'QJ'"/>
      <el-table-column label="水位累计变化量(mm)" align="center" prop="accumulativeValue1" v-if="searchType === 'YL'"/>
      <el-table-column label="处理状态" align="center" width="120">
        <template slot-scope="scope">
          <div class="status-tag" :class="scope.row.remedialMeasures ? 'status-handled' : 'status-unhandled'">
            {{ scope.row.remedialMeasures ? '已处理' : '未处理' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="处理措施" align="center" prop="remedialMeasures" min-width="200">
        <template slot-scope="scope">
          <span v-if="scope.row.remedialMeasures">{{ scope.row.remedialMeasures }}</span>
          <span v-else class="unhandled">未处理</span>
        </template>
      </el-table-column>
      <el-table-column label="处理时间" align="center" prop="remedialTime" width="180">
        <template slot-scope="scope">
          <span v-if="scope.row.remedialTime">{{ parseTime(scope.row.remedialTime) }}</span>
          <span v-else class="unhandled">-</span>
        </template>
      </el-table-column>
      <el-table-column label="处理人" align="center" prop="remedialUid" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.remedialUid">{{ scope.row.remedialUid }}</span>
          <span v-else class="unhandled">-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleRemedial(scope.row)"
            v-hasPermi="['aqy:alarmRecord:remedial']"
          >报警处理
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

    <el-dialog :title="title" :visible.sync="open" :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog>
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="处理措施" prop="remedialMeasures">
              <!-- 快捷选择 -->
              <el-select
                v-model="selectedMeasure"
                placeholder="快捷选择处理措施"
                style="width: 100%; margin-bottom: 10px"
                @change="handleQuickSelect"
              >
                <el-option
                  v-for="item in measureOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <!-- 处理措施输入框 -->
              <el-input
                type="textarea"
                v-model="form.remedialMeasures"
                placeholder="请输入处理措施"
                maxlength="300"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button type="primary" @click="submitDeleteAlarm">取消报警</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listAlarmRecord,
  getAlarmRecord,
  delAlarmRecord,
  addAlarmRecord,
  updateAlarmRecord,
  remedialAlarm
} from "@/api/aqy/alarmRecord";
import {listProject} from "@/api/aqy/project";
import {listAqyEquipment} from "@/api/aqy/aqyEquipment";
import {listAlarmLevel} from "@/api/aqy/alarmLevel";
import {listAqyEquipmentType} from "@/api/aqy/aqyEquipmentType";

export default {
  name: "AlarmRecord",
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
      tableHeight: document.documentElement.scrollHeight - 330,
      // 报警记录表格数据
      alarmRecordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        projectId: null,
        qmtId: null,
        eqmtName: null,
        alarmId: null,
        alarmLevel: null,
        instantValue: null,
        accumulativeValue: null,
        alarmColor: null,
        alarmContent: null,
        recordTime: null,
        eqmtTypeId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        remedialMeasures: [
          { required: true, message: '请输入处理措施', trigger: 'blur' }
        ]
      },
      projectOptions: [],
      eqmtTypeOptions: [],
      eqmtOptions: [],
      alarmLevelOptions: [],
      levelOptions: [
        {id: 3, name: '三级'},
        {id: 2, name: '二级'},
        {id: 1, name: '一级'},
      ],
      searchType: null,
      radioMessage: null,
      // 处理措施选项
      measureOptions: [
        { label: '已人工勘察', value: '已人工勘察' },
        { label: '数据波动，关闭报警', value: '数据波动，关闭报警' },
        { label: '设备故障，已维修', value: '设备故障，已维修' },
        { label: '误报警，已确认', value: '误报警，已确认' },
        { label: '其他情况', value: '其他情况' }
      ],
      // 快捷选择的值
      selectedMeasure: ''
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

        this.getEquipmentTypes(this.queryParams.projectId);
      });
    },
    getEquipmentTypes(projectId) {
      listAqyEquipmentType({ projectId: projectId }).then(response => {
        this.eqmtTypeOptions = response.rows;
        if (response.rows)
          this.queryParams.eqmtTypeId = response.rows[0].id;
        else
          this.queryParams.eqmtTypeId = null;

        this.getAlarmLevels(this.queryParams.eqmtTypeId);
        this.handleEmqtTypeChange(this.queryParams.eqmtTypeId);
      });
    },
    /** 查询报警记录列表 */
    getList() {
      this.loading = true;
      let eqmtTypeItem = this.eqmtTypeOptions.filter(item => {
        return item.id === this.queryParams.eqmtTypeId;
      });
      if (eqmtTypeItem && eqmtTypeItem.length > 0) {
        this.searchType = eqmtTypeItem[0].eqmtTypeSymbol;
      }
      listAlarmRecord(this.queryParams).then(response => {
        this.alarmRecordList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getEquipments(val) {
      listAqyEquipment({eqmtTypeId: val}).then(response => {
        this.eqmtOptions = response.rows;
        if(this.eqmtOptions){
          this.queryParams.qmtId = this.eqmtOptions[0].id;
        }else{
          this.queryParams.qmtId = null;
        }

        this.getList();
      });
    },
    getAlarmLevels(val) {
      listAlarmLevel({eqmtType: val}).then(response => {
        this.alarmLevelOptions = response.rows;
      });
    },
    handleEmqtTypeChange(val) {
      // 清空设备名称的选择
      this.queryParams.eqmtId = null;
      this.eqmtOptions = [];

      if (val) {
        // 更新searchType
        let eqmtTypeItem = this.eqmtTypeOptions.find(item => item.id === val);
        if (eqmtTypeItem) {
          this.searchType = eqmtTypeItem.eqmtTypeSymbol;
        }

        this.getEquipments(val);
      }
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
        qmtId: null,
        eqmtName: null,
        alarmId: null,
        alarmLevel: null,
        instantValue: null,
        accumulativeValue: null,
        alarmColor: null,
        alarmContent: null,
        recordTime: null,
        remedialMeasures: '已人工勘察', // 默认值
        remedialTime: null,
        remedialUid: null,
      };
      this.selectedMeasure = '已人工勘察'; // 重置快捷选择
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
      this.title = "添加报警记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAlarmRecord(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改报警记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 合并处理措施和详细说明
          if (this.form.remedialMeasuresDetail &&
              this.form.remedialMeasures === '其他情况') {
            this.form.remedialMeasures = this.form.remedialMeasuresDetail;
          }

          if (this.form.id != null) {
            updateAlarmRecord(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAlarmRecord(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除报警记录编号为"' + ids + '"的数据项？').then(function() {
        return delAlarmRecord(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('aqy/alarmRecord/export', {
        ...this.queryParams
      }, `alarmRecord_${new Date().getTime()}.xlsx`)
    },
    handleRemedial(row) {
      this.reset();
      this.form = { ...row };
      // 如果没有处理措施，设置默认值
      if (!this.form.remedialMeasures) {
        this.form.remedialMeasures = '已人工勘察';
        this.selectedMeasure = '已人工勘察';
      } else {
        // 如果已有处理措施，尝试匹配预设选项
        const matchedOption = this.measureOptions.find(
          option => option.value === this.form.remedialMeasures
        );
        this.selectedMeasure = matchedOption ? matchedOption.value : '';
      }
      this.open = true;
      this.title = "报警处理";
    },
    submitDeleteAlarm(){
      this.$refs["form"].validate(valid => {
        if (valid) {
          remedialAlarm(this.form).then(response => {
            this.$modal.msgSuccess("处理成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    handleQuickSelect(value) {
      if (value === '其他情况') {
        this.form.remedialMeasures = ''; // 清空输入框
      } else {
        this.form.remedialMeasures = value; // 填入选择的值
      }
    },
    // 获取报警等级样式类名
    getAlarmLevelClass(level) {
      switch (level) {
        case 1:
          return 'level-first';
        case 2:
          return 'level-second';
        case 3:
          return 'level-third';
        default:
          return '';
      }
    },

    // 获取报警等级文本
    getAlarmLevelText(level) {
      switch (level) {
        case 1:
          return '一级报警';
        case 2:
          return '二级报警';
        case 3:
          return '三级报警';
        default:
          return '';
      }
    }
  }
};
</script>

<style scoped>
.unhandled {
  color: #909399;
  font-style: italic;
}

/* 报警等级样式 */
.alarm-level-tag {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
}

.level-first {
  background-color: #F56C6C;
  border: 1px solid #F56C6C;
  box-shadow: 0 2px 4px rgba(245, 108, 108, 0.2);
}

.level-second {
  background-color: #E6A23C;
  border: 1px solid #E6A23C;
  box-shadow: 0 2px 4px rgba(230, 162, 60, 0.2);
}

.level-third {
  background-color: #909399;
  border: 1px solid #909399;
  box-shadow: 0 2px 4px rgba(144, 147, 153, 0.2);
}

/* 处理状态样式 */
.status-tag {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.status-handled {
  background-color: #f0f9eb;
  border: 1px solid #e1f3d8;
  color: #67c23a;
}

.status-unhandled {
  background-color: #fef0f0;
  border: 1px solid #fde2e2;
  color: #f56c6c;
}

/* 未处理状态的文字样式 */
.unhandled {
  color: #909399;
  font-size: 12px;
}

.el-select {
  width: 100%;
}

.el-textarea {
  margin-top: 10px;
}
</style>
