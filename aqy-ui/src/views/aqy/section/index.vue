<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工程项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择工程项目">
          <el-option v-for="( item,index ) in projectOptions" :key='index' :value="item.id"
                     :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="断面名称" prop="sectionName">
        <el-input
          v-model="queryParams.sectionName"
          placeholder="请输入断面名称"
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
          v-hasPermi="['system:section:add']"
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
          v-hasPermi="['system:section:edit']"
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
          v-hasPermi="['system:section:remove']"
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
          v-hasPermi="['system:section:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="sectionList" @selection-change="handleSelectionChange" border>
      <el-table-column label="工程项目" align="center" prop="projectId">
        <template slot-scope="scope">
          <span>{{ functionProjectName(scope.row.projectId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="断面名称" align="center" prop="sectionName"/>
      <el-table-column label="经度" align="center" prop="longitude"/>
      <el-table-column label="纬度" align="center" prop="latitude"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:section:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:section:remove']"
          >删除
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-unfold"
            @click="handleProfile(scope.row)"
          >详情
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

    <!-- 添加或修改断面信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="工程项目" prop="projectId">
              <!--              <el-input v-model="form.projectId" placeholder="请输入工程项目"/>-->
              <el-select v-model="form.projectId" placeholder="请选择工程项目" style="width: 100%">
                <el-option v-for="(item,index ) in projectOptions" :key='index' :value="item.id"
                           :label="item.name"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="断面名称" prop="sectionName">
              <el-input v-model="form.sectionName" placeholder="请输入断面名称"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="经度" prop="longitude">
              <el-input v-model="form.longitude" placeholder="请输入经度"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="latitude">
              <el-input v-model="form.latitude" placeholder="请输入纬度"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="监测任务" prop="1">
              <el-checkbox-group size="medium" v-model="form.eqmtTypeIds">
                <el-checkbox :label="item.id" border v-for="(item,index ) in aqyEquipmentTypeList" :key="index"   >
                  {{ item.eqmtTypeName }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <section-detail ref="sectionDetail" v-if="detailOpen"/>
  </div>
</template>

<script>
import {listSection, getSection, delSection, addSection, updateSection,listTree} from "@/api/aqy/section";
import {listProject} from "@/api/aqy/project";
import {listAqyEquipmentType} from "@/api/aqy/aqyEquipmentType";
import sectionDetail from "./detail";

export default {
  name: "Section",
  components: {
    "section-detail": sectionDetail
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      detailOpen: false,
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
      // 断面信息表格数据
      sectionList: [],
      projectOptions: [],
      checkboxGroup: [],
      aqyEquipmentTypeList: [],

      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        projectId: null,
        sectionName: null,
        longitude: null,
        latitude: null,
        createUid: null,
        isDelete: null
      },
      // 表单参数
      form: {

      },
      // 表单校验
      rules: {
        projectId: [
          {required: true, message: "项目名称不能为空", trigger: "blur"}
        ],
        sectionName: [
          {required: true, message: "断面名称不能为空", trigger: "blur"}
        ],
      }
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
    /** 查询断面信息列表 */
    getList() {
      this.loading = true;
      listSection(this.queryParams).then(response => {
        this.sectionList = response.rows;
        this.total = response.total;
        this.loading = false;
        this.getProjects();
      });
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
        sectionName: null,
        longitude: null,
        latitude: null,
        createTime: null,
        createUid: null,
        isDelete: null,
        eqmtTypeIds:[]
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
      this.getAqyEquipmentType();
      this.open = true;
      this.title = "添加断面信息";
    },

    getAqyEquipmentType() {
      this.loading = true;
      listAqyEquipmentType().then(response => {
        this.aqyEquipmentTypeList = response.rows;
        this.loading = false;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getAqyEquipmentType();
      const id = row.id || this.ids
      getSection(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改断面信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSection(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSection(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除断面信息编号为"' + ids + '"的数据项？').then(function () {
        return delSection(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('aqy/section/export', {
        ...this.queryParams
      }, `section_${new Date().getTime()}.xlsx`)
    },
    /**
     * 查看详情
     * @param row
     */
    handleProfile(row) {
      this.loading = true;
      this.detailOpen = true;
      this.$nextTick(() => {
        this.$refs.sectionDetail.init(row)
      })
      this.loading = false;
    },
  }
};
</script>
