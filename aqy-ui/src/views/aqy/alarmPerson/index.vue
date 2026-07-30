<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工程项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择工程项目">
          <el-option v-for="( item,index ) in projectOptions" :key='index' :value="item.id"
                     :label="item.name"></el-option>
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
          v-hasPermi="['aqy:alarmPerson:add']"
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
          v-hasPermi="['aqy:alarmPerson:edit']"
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
          v-hasPermi="['aqy:alarmPerson:remove']"
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
          v-hasPermi="['aqy:alarmPerson:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="alarmPersonList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="工程项目" align="center" prop="projectId">
        <template slot-scope="scope">
          <span>{{ functionProjectName(scope.row.projectId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报警等级" align="center" prop="alarmLevel">
        <template slot-scope="scope">
          <span>{{scope.row.alarmLevel === 1 ? '一级' : scope.row.alarmLevel === 2 ? '二级' : scope.row.alarmLevel === 3 ? '三级' : '' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="联系人姓名" align="center" prop="contactPerson"/>
      <el-table-column label="联系方式" align="center" prop="contactPersonNumber"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['aqy:alarmPerson:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['aqy:alarmPerson:remove']"
          >删除
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

    <!-- 添加或修改报警联系人对话框 -->
    <el-dialog :title="title" :visible.sync="open" :close-on-click-modal="false" class="showAll_dialog" append-to-body
               v-el-drag-dialog>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="工程项目" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择工程项目" style="width: 100%"
                     @change="handleChangeProject">
            <el-option v-for="(item,index ) in projectOptions" :key='index' :value="item.id"
                       :label="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="报警等级" prop="alarmLevel">
          <el-select v-model="form.alarmLevel" placeholder="请选择报警等级" style="width: 100%">
            <el-option v-for="(item,index ) in levelOptions" :key='index' :value="item.id"
                       :label="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="联系人姓名" prop="contactPerson">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人姓名"/>
        </el-form-item>
        <el-form-item label="联系方式" prop="contactPersonNumber">
          <el-input v-model="form.contactPersonNumber" placeholder="请输入联系方式"/>
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
  listAlarmPerson,
  getAlarmPerson,
  delAlarmPerson,
  addAlarmPerson,
  updateAlarmPerson
} from "@/api/aqy/alarmPerson";
import {listProject} from "@/api/aqy/project";

export default {
  name: "AlarmPerson",
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
      // 报警联系人表格数据
      alarmPersonList: [],
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
        alarmLevel: null,
        contactPerson: null,
        contactPersonNumber: null,
        createUid: null,
        isDelete: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        projectId: [
          {required: true, message: "请选择工程项目", trigger: "blur"}
        ],

      },
      levelOptions: [
        {id: 3, name: '三级'},
        {id: 2, name: '二级'},
        {id: 1, name: '一级'},
      ],
    };
  },
  created() {
    this.getList();
  },
  methods: {
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
      });
    },

    /** 查询报警联系人列表 */
    getList() {
      this.loading = true;
      listAlarmPerson(this.queryParams).then(response => {
        this.alarmPersonList = response.rows;
        this.total = response.total;
        this.loading = false;
        this.getProjects();
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
        alarmLevel: null,
        contactPerson: null,
        contactPersonNumber: null,
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
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加报警联系人";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAlarmPerson(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改报警联系人";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAlarmPerson(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAlarmPerson(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除报警联系人编号为"' + ids + '"的数据项？').then(function () {
        return delAlarmPerson(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('aqy/alarmPerson/export', {
        ...this.queryParams
      }, `alarmPerson_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
