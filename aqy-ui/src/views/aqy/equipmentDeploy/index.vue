<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工程项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择工程项目">
          <el-option v-for="( item,index ) in projectOptions" :key='index' :value="item.id"
                     :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row>
      <el-col :span="16">
        <div id="map" ref="map"></div>
      </el-col>
      <el-col :span="8">
        <el-descriptions class="margin-top" title="设备名称" :column="1" size="medium" border v-for="item in currentShapInfo" style="margin-left: 10px;">
          <el-descriptions-item>
            <template slot="label">
              <i class="el-icon-user"></i>
              设备名称
            </template>
            {{item.name}}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label">
              <i class="el-icon-location-outline"></i>
              设备编号
            </template>
            {{item.code}}
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="showPointForm" style="border: #5a5e66 1px solid;padding: 10px;margin-top: 10px;">
          <el-form ref="formPoint" :model="formPoint" label-width="80px">
            <el-form-item label="设备类型" prop="eqmtType">
              <el-select v-model="formPoint.eqmtType" placeholder="请选择设备类型" style="width: 100%" @change="handleChangeType">
                <el-option v-for="(item,index ) in eqmtSourceTypeOptions" :key='index' :value="item.id"
                           :label="item.name"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="设备" prop="eqmtId">
              <el-select v-model="formPoint.eqmtId" placeholder="请选择设备" style="width: 100%" v-if="formPoint.eqmtType == 'EQMT' || formPoint.eqmtType == 'ALARM'">
                <el-option v-for="(item,index ) in eqmtOptions" :key='index' :value="item.id"
                           :label="item.eqmtName"></el-option>
              </el-select>
              <el-select v-model="formPoint.eqmtId" placeholder="请选择设备" style="width: 100%" v-if="formPoint.eqmtType == 'GATEWAY'">
                <el-option v-for="(item,index ) in eqmtOptions" :key='index' :value="item.id"
                           :label="item.gatwayName"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="X坐标" prop="mouseX">
              <el-input v-model="formPoint.mouseX" disabled/>
            </el-form-item>
            <el-form-item label="Y坐标" prop="mouseY">
              <el-input v-model="formPoint.mouseY" disabled/>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitForm">修改坐标</el-button>
            <el-button @click="cancel">隐 藏</el-button>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-drawer
      :title="drawerPanel.title"
      :visible.sync="drawerPanel.openDrawer"
      :direction="drawerPanel.direction"
      size="50%">
      <equipment-detail ref="equipmentDetail" v-if="openDrawerType === 'EQMT'"></equipment-detail>
      <gateway-equipment-detail ref="gateWayEquipmentDetail" v-if="openDrawerType === 'GATEWAY_EQMT'"></gateway-equipment-detail>
      <alarm-equipment-detail ref="alarmEquipmentDetail" v-if="openDrawerType === 'ALARM_EQMT'"></alarm-equipment-detail>
    </el-drawer>
  </div>
</template>

<script>
import {getProject, listProject} from "@/api/aqy/project";
import Konva from "konva";
import {listAqyEquipment, updateAqyEquipment} from "@/api/aqy/aqyEquipment";
import {listGatwayEquipment, updateGatwayEquipment} from "@/api/aqy/gatwayEquipment";
import {listAlarmEquipment, updateAlarmEquipment} from "@/api/aqy/alarmEquipment";
import equipmentDetail from "@/views/aqy/equipmentDeploy/eqmtDetail";
import gateWayEquipmentDetail from "@/views/aqy/gatwayEquipment/detail";
import alarmEquipmentDetail from "@/views/aqy/alarmEquipment/detail";

export default {
  name: "AqyEquipmentDeploy",
  components: {
    "equipment-detail": equipmentDetail,
    "gateway-equipment-detail": gateWayEquipmentDetail,
    "alarm-equipment-detail": alarmEquipmentDetail
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      showSearch: true,
      // 查询参数
      queryParams: {
        projectId: null,
      },
      projectOptions: [],
      projectInfo: {},
      eqmts: [],
      gateWays: [],
      alarmEqmts: [],
      stage: null,
      layer: null,
      shape: null,
      currentShapInfo: [],
      image: { src: require("@/assets/images/deploy.png") },
      eqmtShaps: [],
      gateWayShaps: [],
      alarmEqmtShaps: [],
      drawerPanel: {
        title: "",
        openDrawer: false,
        direction: "rtl"
      },
      openDrawerType: null,
      baseWidth: 860,
      baseHeight: 430,
      formPoint: {
        eqmtType: null,
        eqmtId: null,
        mouseX: null,
        mouseY: null
      },
      eqmtSourceTypeOptions: [
        {id: 'EQMT', name: '采集设备'},
        {id: 'ALARM', name: '报警设备'},
        {id: 'GATEWAY', name: '网关设备'},
      ],
      eqmtOptions: [],
      showPointForm: true,
    };
  },
  created() {
    this.getProjects();
  },
  methods: {
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.drawCanvas();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    getProjects() {
      listProject().then(response => {
        this.projectOptions = response.rows;
        if (response.rows)
          this.queryParams.projectId = response.rows[0].id
        else
          this.queryParams.projectId = null;

        this.getProjectInfo();
      });
    },
    getProjectInfo(){
      getProject(this.queryParams.projectId).then(response => {
        this.projectInfo = response.data;
        this.drawCanvas();
      });
    },
    getEqmts(){
      listAqyEquipment({projectId: this.queryParams.projectId}).then(response => {
        this.eqmtShaps = [];
        this.eqmts = response.rows;
        this.drawEqmts();
      });
    },
    getGateWayEqmts(){
      this.gateWayShaps = [];
      listGatwayEquipment({projectId: this.queryParams.projectId}).then(response => {
        this.gateWays = response.rows;
        this.drawGateWayEqmts();
      });
    },
    getAlarmEqmts(){
      this.alarmEqmtShaps = [];
      listAlarmEquipment({projectId: this.queryParams.projectId}).then(response => {
        this.alarmEqmts = response.rows;
        this.drawAlarmEqmts();
      });
    },
    drawCanvas(){
      this.$nextTick(() => {
        this.initKonvaStage();
      })
    },
    initKonvaStage(){
      this.stage && this.stage.clearCache();// 多次绘制清除缓存
      //1实例化stage层
      this.stage = new Konva.Stage({
        container: "map",
        width: this.$refs.map.clientWidth,
        height: this.$refs.map.clientHeight,
      });
      //2实例化layer层
      this.layer = new Konva.Layer();
      var imageObj = new Image();
      //imageObj的this是imagedom对象，不是vc
      var vc_this = this;
      imageObj.onload = function () {
        //3实例化shape层
        var image = new Konva.Image({
          image: imageObj,
          x: 0, // 设置图像的初始位置为舞台中心
          y: 0,
          width: vc_this.stage.width(),
          height: vc_this.stage.height(),
          // draggable: true // 允许拖动图像
        });
        //4将layer层添加到stage层
        vc_this.stage.add(vc_this.layer);
        // 5将shape层添加到layer层
        vc_this.layer.add(image);

        // 缩放
        // vc_this.stage.on('wheel', function(e) {
        //   e.evt.preventDefault(); // 阻止默认滚轮行为
        //
        //   var oldScale = image.scaleX(); // 获取当前缩放比例
        //   var pointer = vc_this.stage.getPointerPosition(); // 获取鼠标指针位置
        //
        //   var zoomAmount = e.evt.deltaY * -0.01; // 根据滚轮滚动方向计算缩放比例变化量
        //   var newScale = oldScale + zoomAmount; // 计算新的缩放比例
        //
        //   // 限制缩放范围
        //   if (newScale > 0.1 && newScale < 10) {
        //     image.scale({ x: newScale, y: newScale }); // 设置新的缩放比例
        //
        //     // 根据缩放比例调整图像位置，使其保持在鼠标指针位置不变
        //     var newPos = {
        //       x: pointer.x - (pointer.x - image.x()) * newScale / oldScale,
        //       y: pointer.y - (pointer.y - image.y()) * newScale / oldScale
        //     };
        //     image.position(newPos);
        //
        //     vc_this.layer.batchDraw(); // 重新绘制图层
        //   }
        // });

        vc_this.getEqmts();
        vc_this.getGateWayEqmts();
        vc_this.getAlarmEqmts();
      };
      imageObj.src = this.image.src;
      this.stage.on("mousedown", (e) => {
        vc_this.formPoint.mouseX = e.evt.offsetX;
        vc_this.formPoint.mouseY = e.evt.offsetY;
        this.showPointForm = true;
      });
    },
    drawEqmts(){
      if(this.eqmts && this.eqmts.length > 0){
        for(let it = 0; it < this.eqmts.length; it++){
          if(this.eqmts[it].longitude != null && this.eqmts[it].longitude > 0 && this.eqmts[it].latitude != null && this.eqmts[it].latitude > 0){
            switch (this.eqmts[it].eqmtTypeData.eqmtTypeSymbol){
              case 'WY':
                if(this.eqmts[it].xorY === 'X')
                  this.drawWyCircle(this.eqmts[it]);
                break;
              case 'LF':
                break;
              case 'QJ':
                this.drawQjTriangle(this.eqmts[it]);
                break;
              case 'YL':
                this.drawYlCircle(this.eqmts[it]);
                break;
            }
          }
        }
      }
    },
    drawGateWayEqmts() {
      if (this.gateWays && this.gateWays.length > 0) {
        for (let it = 0; it < this.gateWays.length; it++) {
          if (this.gateWays[it].longitude != null && this.gateWays[it].longitude > 0
            && this.gateWays[it].latitude != null && this.gateWays[it].latitude > 0) {
            this.drawVisioRect(this.gateWays[it]);
          }
        }
      }
    },
    drawAlarmEqmts(){
      if(this.alarmEqmts && this.alarmEqmts.length > 0){
        for(let it = 0; it < this.alarmEqmts.length; it++){
          if(this.alarmEqmts[it].longitude != null && this.alarmEqmts[it].longitude > 0 && this.alarmEqmts[it].latitude != null && this.alarmEqmts[it].latitude > 0){
            this.drawAlarmRect(this.alarmEqmts[it]);
          }
        }
      }
    },

    drawWyCircle(eqmt) {
      const circle = new Konva.Circle({
        name: "circle",
        x: eqmt.longitude,
        y: eqmt.latitude,
        radius: 7,
        visible: true, //是否显示
        fill: "#ffc000",
        stroke: "#333333",
        draggable: false,
        strokeWidth: 0.5,
        opacity: 0.3,
      });
      var vc_this = this;
      this.layer.add(circle);
      this.layer.draw();
      circle.on("mouseover", (e) => {
        vc_this.currentShapInfo = [];
        vc_this.currentShapInfo.push({
          name: eqmt.eqmtName,
          code: eqmt.eqmtName,
        });
      });
      circle.on("mouseout", (e) => {
        vc_this.currentShapInfo = [];
      });
      circle.on("click", (e) => {
        this.openDrawerType = 'EQMT';
        this.drawerPanel.title = "设备详情";
        this.drawerPanel.openDrawer = true;
        this.$nextTick(() => {
          this.$refs.equipmentDetail.init(eqmt);
        });
      });
      this.eqmtShaps.push(circle);
      return circle;
    },
    drawQjTriangle(eqmt){
      let points = [eqmt.longitude - 6, eqmt.latitude + 8,
        eqmt.longitude, eqmt.latitude - 8,
        eqmt.longitude + 8, eqmt.latitude + 6
      ];
      const poly = new Konva.Line({
        name: "poly",
        points: points,
        fill: "#ffc000",
        stroke: "#904469",
        strokeWidth: 1,
        draggable: false,
        opacity: 0.3,
        lineCap: "round",
        lineJoin: "round",
        closed: true,
        strokeScaleEnabled: false,
      });
      this.layer.add(poly);
      this.layer.draw();
      var vc_this = this;
      poly.on("mouseover", (e) => {
        vc_this.stage.container().style.cursor = "pointer";
        vc_this.currentShapInfo = [];
        vc_this.currentShapInfo.push({
          name: eqmt.eqmtName,
          code: eqmt.eqmtCode,
        });
      });
      poly.on("mouseout", (e) => {
        vc_this.stage.container().style.cursor = "auto";
        vc_this.currentShapInfo = [];
      });
      poly.on("click", (e) => {
        this.openDrawerType = 'EQMT';
        this.drawerPanel.title = "设备详情";
        this.drawerPanel.openDrawer = true;
        this.$nextTick(() => {
          this.$refs.equipmentDetail.init(eqmt);
        });
      });
      this.eqmtShaps.push(poly);
      return poly;
    },
    drawYlCircle(eqmt) {
      const circle = new Konva.Circle({
        name: "circle",
        x: eqmt.longitude,
        y: eqmt.latitude,
        radius: 10,
        visible: true, //是否显示
        fill: "#ffc000",
        stroke: "#333333",
        draggable: false,
        strokeWidth: 0.5,
        opacity: 0.3,
      });
      var vc_this = this;
      this.layer.add(circle);
      this.layer.draw();
      circle.on("mouseover", (e) => {
        vc_this.currentShapInfo = [];
        vc_this.currentShapInfo.push({
          name: eqmt.eqmtName,
          code: eqmt.eqmtName,
        });
      });
      circle.on("mouseout", (e) => {
        vc_this.currentShapInfo = [];
      });
      circle.on("click", (e) => {
        this.openDrawerType = 'EQMT';
        this.drawerPanel.title = "设备详情";
        this.drawerPanel.openDrawer = true;
        this.$nextTick(() => {
          this.$refs.equipmentDetail.init(eqmt);
        });
      });
      this.eqmtShaps.push(circle);
      return circle;
    },
    drawVisioRect(gateWay) {
      const rect = new Konva.Rect({
        name: "rect",
        x: gateWay.longitude - 8,
        y: gateWay.latitude - 6,
        width: 20,
        height: 12,
        fill: "green",
        stroke: "green",
        strokeWidth: 1,
        opacity: 0.3,
        draggable: true,
        strokeScaleEnabled: false,
      });
      this.layer.add(rect);
      this.layer.draw();
      var vc_this = this;
      rect.on("mouseover", (e) => {
        vc_this.currentShapInfo = [];
        vc_this.currentShapInfo.push({
          name: gateWay.gatwayName,
          code: gateWay.gatwayCode,
        });
      });
      rect.on("mouseout", (e) => {
        vc_this.currentShapInfo = [];
      });
      rect.on("click", (e) => {
        this.openDrawerType = 'GATEWAY_EQMT';
        this.drawerPanel.title = "网关设备详情";
        this.drawerPanel.openDrawer = true;
        this.$nextTick(() => {
          this.$refs.gateWayEquipmentDetail.init(gateWay);
        });
      });
      this.gateWayShaps.push(rect);
      return rect;
    },
    drawAlarmRect(gateWay) {
      const rect = new Konva.Rect({
        name: "rect",
        x: gateWay.longitude - 10,
        y: gateWay.latitude - 8,
        width: 20,
        height: 20,
        fill: "green",
        stroke: "green",
        strokeWidth: 1,
        opacity: 0.3,
        draggable: true,
        strokeScaleEnabled: false,
      });
      this.layer.add(rect);
      this.layer.draw();
      var vc_this = this;
      rect.on("mouseover", (e) => {
        vc_this.currentShapInfo = [];
        vc_this.currentShapInfo.push({
          name: gateWay.eqmtName,
          code: gateWay.eqmtCode,
        });
      });
      rect.on("mouseout", (e) => {
        vc_this.currentShapInfo = [];
      });
      rect.on("click", (e) => {
        this.openDrawerType = 'ALARM_EQMT';
        this.drawerPanel.title = "报警设备详情";
        this.drawerPanel.openDrawer = true;
        this.$nextTick(() => {
          this.$refs.alarmEquipmentDetail.init(gateWay);
        });
      });
      this.alarmEqmtShaps.push(rect);
      return rect;
    },
    handleChangeType(){
      if(this.formPoint.eqmtType){
        switch (this.formPoint.eqmtType){
          case 'EQMT':
            listAqyEquipment({projectId: this.queryParams.projectId}).then(response => {
              this.eqmtOptions = response.rows;
            });
            break;
          case 'ALARM':
            listAlarmEquipment({projectId: this.queryParams.projectId}).then(response => {
              this.eqmtOptions = response.rows;
            });
            break;
          case 'GATEWAY':
            listGatwayEquipment({projectId: this.queryParams.projectId}).then(response => {
              this.eqmtOptions = response.rows;
            });
            break;
        }
      }
    },
    submitForm(){
      if(!this.formPoint.eqmtId){
        this.$modal.msgError("请选择相应设备");
        return;
      }
      if(this.formPoint.eqmtType){
        switch (this.formPoint.eqmtType){
          case 'EQMT':
            updateAqyEquipment({
              id: this.formPoint.eqmtId,
              longitude: this.formPoint.mouseX,
              latitude: this.formPoint.mouseY,
            }).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.initKonvaStage();
            });
            break;
          case 'ALARM':
            updateAlarmEquipment({
              id: this.formPoint.eqmtId,
              longitude: this.formPoint.mouseX,
              latitude: this.formPoint.mouseY,
            }).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.initKonvaStage();
            });
            break;
          case 'GATEWAY':
            updateGatwayEquipment({
              id: this.formPoint.eqmtId,
              longitude: this.formPoint.mouseX,
              latitude: this.formPoint.mouseY,
            }).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.initKonvaStage();
            });
            break;
        }
      }
    },
    cancel(){
      this.formPoint = {
        eqmtType: null,
        eqmtId: null,
        mouseX: null,
        mouseY: null
      };
      this.showPointForm = false;
    }
  }
};
</script>

<style>
  #map {
    background: #ddd;
    overflow: hidden;
    width: 860px;
    height: 483px;
    border: #5a5e66 1px solid;
  }
</style>
