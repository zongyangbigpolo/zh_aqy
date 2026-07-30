  <template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工程项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择工程项目">
          <el-option v-for="( item,index ) in projectOptions" :key='index' :value="item.id"
                     :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属网关" prop="gatwayId">
        <el-select v-model="queryParams.gatwayId" placeholder="请选择所属网关">
          <el-option v-for="( item,index ) in gatwayOptions" :key='index' :value="item.id"
                     :label="item.gatwayName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备类型" prop="eqmtTypeId">
        <el-select v-model="queryParams.eqmtTypeId" placeholder="请选择设备类型">
          <el-option v-for="( item,index ) in eqmtTypeOptions" :key='index' :value="item.id"
                     :label="item.eqmtTypeName"></el-option>
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
          v-hasPermi="['aqy:aqyEquipment:add']"
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
          v-hasPermi="['aqy:aqyEquipment:edit']"
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
          v-hasPermi="['aqy:aqyEquipment:remove']"
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
          v-hasPermi="['aqy:aqyEquipment:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="aqyEquipmentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="所属智能网关" align="center" prop="gatwayId">
        <template slot-scope="scope">
          <span>{{ functionGatwayName(scope.row.gatwayId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="设备类型" align="center" prop="eqmtTypeId">
        <template slot-scope="scope">
          <span>{{ functionAqyEquipmentType(scope.row.eqmtTypeId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="设备名称" align="center" prop="eqmtName"/>
      <el-table-column label="设备编码" align="center" prop="eqmtCode" width="150"/>
      <el-table-column label="靶标序号" align="center" prop="sortNum"/>
      <el-table-column label="数据计量单位" align="center" prop="unitName"/>
      <el-table-column label="视觉测量仪名称" align="center" prop="visualEqmtName"/>
      <el-table-column label="视觉测量仪编码" align="center" prop="visualEqmtCode"/>
      <el-table-column label="X/Y" align="center" prop="xorY"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['aqy:aqyEquipment:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['aqy:aqyEquipment:remove']"
          >删除
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-unfold"
            @click="handleProfile(scope.row)"
          >详情
          </el-button>
          <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)"
                       v-hasPermi="['system:user:resetPwd', 'system:user:edit']">
            <el-button size="mini" type="text" icon="el-icon-d-arrow-right">更多</el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="handleCatchImage" icon="el-icon-camera"
                                v-hasPermi="['system:user:resetPwd']">抓取照片
              </el-dropdown-item>
              <el-dropdown-item command="handleShowImages" icon="el-icon-picture-outline"
                                v-hasPermi="['system:user:edit']">查看拍照记录
              </el-dropdown-item>
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

    <!-- 添加或修改数据采集设备对话框 -->
    <el-dialog :title="title" :visible.sync="open" :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog>
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="工程项目" prop="projectId">
              <el-select v-model="form.projectId" placeholder="请选择工程项目" style="width: 100%"
                         @change="handleChangeProject">
                <el-option v-for="(item,index ) in projectOptions" :key='index' :value="item.id"
                           :label="item.name"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备类型" prop="eqmtTypeId">
              <el-select v-model="form.eqmtTypeId" placeholder="请选择设备类型" style="width: 100%">
                <el-option v-for="( item,index ) in eqmtTypeOptions" :key='index' :value="item.id"
                           :label="item.eqmtTypeName"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否可以抓取照片" prop="canCatchImage">
              <el-checkbox v-model="form.canCatchImage" label="true" border style="width: 100%"></el-checkbox>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属网关" prop="projectId">
              <el-select v-model="form.gatwayId" placeholder="请选择所属网关" style="width: 100%">
                <el-option v-for="(item,index ) in gatwayOptions" :key='index' :value="item.id"
                           :label="item.gatwayName"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="设备名称" prop="eqmtName">
              <el-input v-model="form.eqmtName" placeholder="请输入设备名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设备编码" prop="eqmtCode">
              <el-input v-model="form.eqmtCode" placeholder="请输入设备编码"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="靶标序号" prop="sortNum">
              <el-input v-model="form.sortNum" placeholder="请输入靶标序号，只针对视觉监测设备"/>
            </el-form-item>
          </el-col>
        </el-row>
<!--        <el-row>-->
<!--          <el-col :span="12">-->
<!--            <el-form-item label="经度" prop="longitude">-->
<!--              <el-input v-model="form.longitude" type="number" placeholder="请输入经度"/>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
<!--          <el-col :span="12">-->
<!--            <el-form-item label="纬度" prop="latitude">-->
<!--              <el-input v-model="form.latitude" type="number" placeholder="请输入纬度"/>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
<!--        </el-row>-->
<!--        <el-row>-->
<!--          <el-col :span="12">-->
<!--            <el-form-item label="高程" prop="elevation">-->
<!--              <el-input v-model="form.elevation" type="number" placeholder="请输入高程"/>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
<!--          <el-col :span="12">-->
<!--            <el-form-item label="方位角" prop="azimuthAngle">-->
<!--              <el-input v-model="form.azimuthAngle" type="number" placeholder="请输入方位角"/>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
<!--        </el-row>-->
        <el-row>
          <el-col :span="12">
            <el-form-item label="初始坐标X" prop="initialX">
              <el-input v-model="form.initialX" type="number" placeholder="请输入初始坐标X"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="初始坐标Y" prop="initialY">
              <el-input v-model="form.initialY" type="number" placeholder="请输入初始坐标Y"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="初始坐标H" prop="initialH">
              <el-input v-model="form.initialH" type="number" placeholder="请输入初始坐标H"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unitName">
              <el-input v-model="form.unitName" placeholder="请输入数据计量单位"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="视觉测量仪名称" prop="visualEqmtName">
              <el-input v-model="form.visualEqmtName" placeholder="请输入视觉测量仪名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="视觉测量仪编码" prop="visualEqmtCode">
              <el-input v-model="form.visualEqmtCode" placeholder="请输入视觉测量仪编码"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="X/Y" prop="xorY">
              <el-select v-model="form.xorY" placeholder="请选择X坐标或Y坐标">
                <el-option v-for="( item,index ) in xYOptions" :key='index' :value="item.id"
                           :label="item.name"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" placeholder="请输入备注"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="采集任务" prop="qrtzJobId">
              <el-select v-model="form.qrtzJobId" placeholder="请选择采集任务">
                <el-option v-for="( item,index ) in jobOptions" :key='index' :value="item.jobId"
                           :label="item.jobName"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="titleProfile" :visible.sync="openProfile" width="80%" :close-on-press-escape="false"
               :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog>
      <equipment-detail ref="equipmentDetail"></equipment-detail>
    </el-dialog>
  </div>
</template>

<script>
import {
  listAqyEquipment,
  getAqyEquipment,
  delAqyEquipment,
  addAqyEquipment,
  updateAqyEquipment
} from "@/api/aqy/aqyEquipment";
import {listProject} from "@/api/aqy/project";
import {listAqyEquipmentType} from "@/api/aqy/aqyEquipmentType";
import {listGatwayEquipment} from "@/api/aqy/gatwayEquipment";
import {downConfig} from "@/api/aqy/cmdMqtt";
import equipmentDetail from "./detail";
import {listJob} from "@/api/monitor/job";

export default {
  name: "AqyEquipment",
  components: {
    "equipment-detail": equipmentDetail
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
      // 数据采集设备表格数据
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
        gatwayId: null,
        eqmtTypeId: null,
        eqmtName: null,
        eqmtCode: null,
        longitude: null,
        latitude: null,
        elevation: null,
        azimuthAngle: null,
        initialX: null,
        initialY: null,
        initialH: null,
        accumulativeChangeValue: null,
        instantChangeValue: null,
        alarmLevel: null,
        onlineStatus: null,
        createUid: null,
        isDelete: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        projectId: [
          {required: true, message: "工程项目不能为空", trigger: "blur"}
        ],
        gatwayId: [
          {required: true, message: "所属网关不能为空", trigger: "blur"}
        ],
        eqmtTypeId: [{required: true, message: "设备类型不能为空", trigger: "blur"}],
        eqmtName: [{required: true, message: "设备名称不能为空", trigger: "blur"}],

        eqmtCode: [
          {required: true, message: "设备编码不能为空", trigger: "blur"}
        ],
      },
      projectOptions: [],
      gatwayOptions: [],
      eqmtTypeOptions: [],
      aqyEquipmentTypeList: [],
      titleProfile: "",
      openProfile: false,
      equipmentDetails: {},
      xYOptions: [
        {id: 'X', name: 'X'},
        {id: 'Y', name: 'Y'},
      ],
      jobOptions: [],
    };
  },
  created() {
    this.getProjects();
    this.getJobOptions();
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
        this.getGatways(this.queryParams.projectId);
        this.getList();
      });
    },
    getJobOptions() {
      listJob().then(response => {
        this.jobOptions = response.rows;
      });
    },
    /** 查询数据采集设备列表 */
    getList() {
      this.loading = true;
      listAqyEquipment(this.queryParams).then(response => {
        this.aqyEquipmentList = response.rows;
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
        gatwayId: null,
        eqmtTypeId: null,
        eqmtName: null,
        eqmtCode: null,
        sortNum: null,
        longitude: null,
        latitude: null,
        elevation: null,
        azimuthAngle: null,
        initialX: null,
        initialY: null,
        initialH: null,
        accumulativeChangeValue: null,
        instantChangeValue: null,
        unitName: null,
        alarmLevel: null,
        onlineStatus: null,
        remark: null,
        createTime: null,
        createUid: null,
        isDelete: null,
        visualEqmtName: null,
        visualEqmtCode: null,
        xorY: null,
        shouldCreateQrtzJob: false,
        qrtzJobId: null,
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
      this.title = "添加数据采集设备";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAqyEquipment(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改数据采集设备";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAqyEquipment(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAqyEquipment(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除数据采集设备编号为"' + ids + '"的数据项？').then(function () {
        return delAqyEquipment(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('aqy/aqyEquipment/export', {
        ...this.queryParams
      }, `aqyEquipment_${new Date().getTime()}.xlsx`)
    },
    handleChangeProject() {
      this.getEquipmentTypes(this.form.projectId);
      this.getGatways(this.form.projectId);
    },
    getEquipmentTypes(projectId) {
      listAqyEquipmentType({projectId: projectId}).then(response => {
        this.eqmtTypeOptions = response.rows;
      });
    },
    functionAqyEquipmentType(eqmtTypeId) {
      const eqmtType = this.eqmtTypeOptions.find(item => item.id === eqmtTypeId)
      return eqmtType ? eqmtType.eqmtTypeName : ''
    },
    getGatways(projectId) {
      listGatwayEquipment({projectId: projectId}).then(response => {
        this.gatwayOptions = response.rows;
      });
    },
    functionGatwayName(gatwayId) {
      const gatway = this.gatwayOptions.find(item => item.id === gatwayId)
      return gatway ? gatway.gatwayName : ''
    },

    /**
     * 查看详情
     * @param row
     */
    handleProfile(row) {
      this.titleProfile = "【" + row.eqmtName + "】设备详情";
      this.openProfile = true
      this.$nextTick(() => {
        this.$refs.equipmentDetail.init(row)
      })

    },
    // 更多操作触发
    handleCommand(command, row) {
      switch (command) {
        case "handleCatchImage":
          this.handleCatchImage(row);
          break;
        case "handleShowImages":
          this.handleShowImages(row);
          break;
        default:
          break;
      }
    },
    /**
     * 下发网关
     */
    handleCatchImage(row) {
    },
    /**
     * 下发网关
     */
    handleShowImages(row) {

    },
  }
};
</script>
