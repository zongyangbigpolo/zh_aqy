<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工程项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择工程项目">
          <el-option v-for="( item,index ) in projectOptions" :key='index' :value="item.id"
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
          v-hasPermi="['system:camera:add']"
        >新增
        </el-button>
      </el-col>
      <!--      <el-col :span="1.5">-->
      <!--        <el-button-->
      <!--          type="success"-->
      <!--          plain-->
      <!--          icon="el-icon-edit"-->
      <!--          size="mini"-->
      <!--          :disabled="single"-->
      <!--          @click="handleUpdate"-->
      <!--          v-hasPermi="['system:camera:edit']"-->
      <!--        >修改-->
      <!--        </el-button>-->
      <!--      </el-col>-->
      <!--      <el-col :span="1.5">-->
      <!--        <el-button-->
      <!--          type="danger"-->
      <!--          plain-->
      <!--          icon="el-icon-delete"-->
      <!--          size="mini"-->
      <!--          :disabled="multiple"-->
      <!--          @click="handleDelete"-->
      <!--          v-hasPermi="['system:camera:remove']"-->
      <!--        >删除-->
      <!--        </el-button>-->
      <!--      </el-col>-->
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:camera:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="cameraList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="工程项目" align="center" prop="projectId">
        <template slot-scope="scope">
          <span>{{ functionProjectName(scope.row.projectId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="摄像头名称" align="center" prop="eqmtName"/>
      <el-table-column label="设备编号" align="center" prop="qmtCode"/>
      <el-table-column label="设备状态" align="center" prop="onlineStatus">
        <template slot-scope="scope">

          <dict-tag :options="dict.type.online_status" :value="scope.row.onlineStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handlePreview(scope.row)"
            v-hasPermi="['system:camera:view']"
          >预览
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-document"
            @click="handleDetail(scope.row)"
            v-hasPermi="['system:camera:detail']"
          >详情
          </el-button>
          <!--          <el-button-->
          <!--            size="mini"-->
          <!--            type="text"-->
          <!--            icon="el-icon-edit"-->
          <!--            @click="handleUpdate(scope.row)"-->
          <!--            v-hasPermi="['system:camera:edit']"-->
          <!--          >修改-->
          <!--          </el-button>-->
          <!--          <el-button-->
          <!--            size="mini"-->
          <!--            type="text"-->
          <!--            icon="el-icon-delete"-->
          <!--            @click="handleDelete(scope.row)"-->
          <!--            v-hasPermi="['system:camera:remove']"-->
          <!--          >删除-->
          <!--          </el-button>-->
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

    <!-- 添加或修改监控摄像头对话框 -->
    <el-dialog :title="title" :visible.sync="open"  :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="工程项目" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择工程项目" style="width: 100%">
            <el-option v-for="(item,index ) in projectOptions" :key='index' :value="item.id"
                       :label="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备名称" prop="eqmtName">
          <el-input v-model="form.eqmtName" placeholder="请输入摄像头名称"/>
        </el-form-item>
        <el-form-item label="设备编号" prop="qmtCode">
          <el-input v-model="form.qmtCode" placeholder="请输入设备编号"/>
        </el-form-item>
        <el-form-item label="IP地址" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入IP地址"/>
        </el-form-item>
        <el-form-item label="端口号" prop="port">
          <el-input v-model="form.port" placeholder="请输入端口号"/>
        </el-form-item>
        <el-form-item label="登录账号" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入登录账号"/>
        </el-form-item>
        <el-form-item label="登录密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入登录密码"/>
        </el-form-item>
        <el-form-item label="是否显示到大屏" prop="showFront">
          <el-input v-model="form.showFront" placeholder="请输入是否显示到大屏"/>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>


    <el-dialog
      :title="viewTitle"
      :visible.sync="viewOpen"
      width="80%"
      :close-on-click-modal="false"
      append-to-body
      @close="handlePreviewClose"
    >
      <iframe
        :src="palyUrl"
        style="margin: 0 auto; display: block;"
        width="100%"
        height="600"
        id="ysOpenDevice"
        allowfullscreen
      >
      </iframe>
    </el-dialog>

    <camera-view ref="cameraView" v-if="detailOpen"></camera-view>

  </div>
</template>

<script>
import {
  listCamera,
  getCamera,
  delCamera,
  addCamera,
  updateCamera,
  getAccessToken,
  getWebVideoUrl,
  getAccessToken2,
  getStructures,
  getMeasareas,
  getMeaspointData,
  getAggregate
} from "@/api/aqy/camera";
import {listProject} from "@/api/aqy/project";
import cameraView from "./detail";

export default {
  name: "Camera",
  components: {
    "camera-view": cameraView
  },

  dicts: ['online_status'],
  data() {
    return {

      hikParams: {
        hikToken: "",
        deviceSerial: "",
        deviceSerialList: [],
        channelNo: 1,
        videoLevel: 1,
      },
      palyUrl: "",
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
      // 监控摄像头表格数据
      cameraList: [],
      projectOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      viewTitle: "",
      viewOpen: false,
      detailOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        projectId: null,
        eqmtName: null,
        qmtCode: null,
        ip: null,
        port: null,
        userName: null,
        password: null,
        showFront: null,
        onlineStatus: null,
        createUid: null,
        isDelete: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},
      viewTimer: null,  // 添加预览定时器
    };
  },
  created() {
    this.getAccessToken();
    // this.getAccessToken2()
    // this.getStructures()
    // this.getMeasareas()
    // this.getMeaspointData()
    // this.getAggregate()
    this.getList();
  },

  methods: {
    getAccessToken() {
      getAccessToken().then(response => {
        this.hikParams.hikToken = response.data.access_token
        console.log(this.hikParams)
      });
    },

    getAccessToken2() {
      getAccessToken2().then(response => {

      });
    },

    getStructures(){
      const id = 160
      getStructures(id).then(response => {

      });
    },


    getMeasareas(){
      const id = 906
      getMeasareas(id).then(response => {

      });
    },



    getMeaspointData(){
      const id = 906
      const st = 1728524135
      const et = 1728524435
      getMeaspointData(id, st, et).then(response => {

      });
    },
    getAggregate(){
      const measItemId = 84457
      getAggregate(measItemId).then(response => {

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
    functionProjectName(projectId) {
      const project = this.projectOptions.find(item => item.id === projectId)
      return project ? project.name : ''
    },
    /** 查询监控摄像头列表 */
    getList() {
      this.loading = true;
      listCamera(this.queryParams).then(response => {
        this.cameraList = response.rows;
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
        eqmtName: null,
        qmtCode: null,
        ip: null,
        port: null,
        userName: null,
        password: null,
        showFront: null,
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
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    //监控预览
    handlePreview(row) {
      this.viewTitle = "监控预览"
      this.hikParams.deviceSerial = row.qmtCode
      this.viewOpen = true

      if (row.type === 2) {
        this.palyUrl = `https://open.ys7.com/console/jssdk/pc.html?accessToken=${row.accessToken}&url=ezopen://open.ys7.com/${row.qmtCode}/1.hd.live`;
      } else {
        getWebVideoUrl(this.hikParams).then(response => {
          this.palyUrl = response.data.data.previewUrl
        });
      }

      // 添加2分钟自动关闭
      if (this.viewTimer) {
        clearTimeout(this.viewTimer);
      }
      this.viewTimer = setTimeout(() => {
        this.handlePreviewClose();
        this.$message.info('预览超时，已自动关闭');
      }, 30000);
    },

    // 添加处理关闭预览的方法
    handlePreviewClose() {
      this.viewOpen = false;
      // 清空 iframe 的 src，停止视频流
      this.palyUrl = '';
      if (this.viewTimer) {
        clearTimeout(this.viewTimer);
      }
    },

    // 组件销毁时清除定时器
    beforeDestroy() {
      if (this.viewTimer) {
        clearTimeout(this.viewTimer);
      }
    },

    //监控详情
    handleDetail(row) {
      this.detailOpen = true
      this.$nextTick(() => {
        this.hikParams.deviceSerialList = [row.qmtCode]

        this.$refs.cameraView.init(this.hikParams,row)
      })
    },


    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加监控摄像头";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCamera(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改监控摄像头";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCamera(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCamera(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除监控摄像头编号为"' + ids + '"的数据项？').then(function () {
        return delCamera(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/camera/export', {
        ...this.queryParams
      }, `camera_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
