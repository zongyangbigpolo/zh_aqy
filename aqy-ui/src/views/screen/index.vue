<template>
  <html lang="en">
    <body>
    <!-- 头部模块 -->
    <header>
      <h1><span>大黄山边坡安全云平台</span></h1>
      <p>
        <span class="nowTime">{{nowTime}}</span>
        <span style="margin-left: 20px;">
          {{projectInfo.city}} <i :class="getWeatherIcon(weatherInfo.type)"></i> 风力：{{ weatherInfo.windPower }} {{ weatherInfo.temperature }}℃
        </span>
      </p>
    </header>
    <div class="main">
      <!-- 基本信息 -->
      <div class="project">
        <div class="project-title">
          <span>项目基本信息</span>
        </div>
        <div class="project-content">
          <div class="select" tabindex="0">
            <select class="select-div" v-model="projectId">
              <option v-for="( item,index ) in projectOptions" :key='index' :value="item.id"
                         :label="item.name"></option>
            </select>
          </div>
          <div class="project-list">
            <p class="content">{{projectInfo.projectDesc}}</p>
            <ul>
              <li>项目类型：<dict-tag :options="dict.type.project_type" :value="projectInfo.projectType"/></li>
              <li>所属企业：{{projectInfo.companyName}}</li>
            </ul>
          </div>
        </div>
      </div>

      <!-- 项目分布 -->
      <div class="projectMap">
        <div class="projectMap-title">
          <span>项 目 分 布</span>
        </div>
        <div class="projectMap-content">
          <div class="map" id="map" ref="mapCharts">
          </div>
        </div>
      </div>

      <!-- 地表位移变化 -->
      <div class="shift">
        <div class="shift-title">
          <span>地 表 位 移 变 化 (单位mm)</span>
        </div>
        <div class="shift-content">
          <div class="shiftChart" id="shiftChart">

          </div>
        </div>
      </div>

      <!-- 数据状况 -->
      <div class="total">
        <div v-for="(item, index) in eqmtByTypes" :class="`data${index+1}`">
          <span>{{item.eqmtTypeName}}</span>
          <p v-if="item.eqmtTypeSymbol === 'WY'">
            x：{{item.accumulativeChangeValueX}}{{item.unitName}}</br>y：{{item.accumulativeChangeValueY}}{{item.unitName}}
          </p>
          <p v-if="item.eqmtTypeSymbol === 'LF'">
            {{item.accumulativeChangeValueX}}{{item.unitName}}
          </p>
          <p v-if="item.eqmtTypeSymbol === 'QJ'">
            x: {{item.accumulativeChangeValueX}}{{item.unitName}}</br>y：{{item.accumulativeChangeValueY}}{{item.unitName}}
            </br>z：{{item.accumulativeChangeValueZ}}{{item.unitName}}
          </p>
          <p v-if="item.eqmtTypeSymbol === 'YL'">
            {{item.accumulativeChangeValueX}}{{item.unitName}}
          </p>
        </div>
        <canvas class="rain"></canvas>
        <canvas class="dashed"></canvas>
        <div class="sphere">
          <div class="sphere-bg"></div>
          <div class="sum">
            <p class="alarm-p" v-if="alarmStatusData.hasAlarm">
              <span v-for="item in alarmStatusData.items"
                    v-if="alarmStatusData.hasAlarm && item.maxAlarmLevel > 0"
                    :key="item.eqmtName"
                    :class="[
                      'alarm-item',
                      alarmStatusData.maxLevel === 3 ? 'spanColor3 alarmSpeed3'
                      : alarmStatusData.maxLevel === 2 ? 'spanColor2 alarmSpeed2'
                      : alarmStatusData.maxLevel === 1 ? 'spanColor1 alarmSpeed1'
                      : ''
                    ]">
                <i class="el-icon-warning warning-icon"></i>{{item.eqmtName}} {{item.maxAlarmLevel}}级报警
              </span>
            </p>
            <p class="normal-p" v-else>
              <i class="el-icon-success"></i>系统运行正常
            </p>
          </div>
        </div>
        <div class="cicle3"></div>
        <div class="cicle4"></div>
        <div class="cicle5"></div>
        <div class="cicle6"></div>
        <div class="cicle7"></div>
        <div v-for="(item, index) in eqmtByTypes" :class="`cicle${index+8}`">
          <span>{{item.onlineCount}}/{{item.totalCount}}</span>
          <p>{{item.eqmtTypeName}}</p>
        </div>
      </div>

      <!-- 雨量变化 -->
      <div class="rain">
        <div class="rain-title">
          <span>雨 量 变 化 (单位mm)</span>
        </div>
        <div class="rain-content">
          <div class="rainChart" id="rainChart">

          </div>
        </div>
      </div>

      <!-- 裂缝变化 -->
      <div class="fissure">
        <div class="fissure-title">
          <span>裂 缝 变 化 (单位mm)</span>
        </div>
        <div class="fissure-content">
          <div class="fissureChart" id="fissureChart">

          </div>
        </div>
      </div>

      <!-- 倾角变化 -->
      <div class="dipAngle">
        <div class="dipAngle-title">
          <span>倾 角 变 化 (单位°)</span>
        </div>
        <div class="dipAngle-content">
          <div class="dipAngleChart" id="dipAngleChart">

          </div>
        </div>
      </div>

    </div>

    <!-- 底部模块 -->
<!--    <div class="bottom">-->
<!--      <h5><span>数据状况</span></h5>-->
<!--    </div>-->
    </body>
  </html>
</template>

<script>
import $ from '@/api/screen/jquery-1.11.0.min';
import * as echarts from "echarts";
import 'echarts-liquidfill';
import './china.js'
import './css/style.css';
import {dashed, rainBg} from './main.js'
import moment from "moment";
import {getProject, listProject} from "@/api/aqy/project";
import {listEqmtTypeByProjectId} from "@/api/aqy/aqySectionEqmt";
import {listEqmtsGroupByType, queryEquipmentAlarmStatus} from "@/api/aqy/aqyEquipment";
import {listWyRawForCharts} from "@/api/aqy/aqyEquipmentWyRaw";
import {listYlRawForCharts} from "@/api/aqy/aqyEquipmentYlRaw";
import {listLfRawForCharts} from "@/api/aqy/aqyEquipmentLfRaw";
import {listQjRawForCharts} from "@/api/aqy/aqyEquipmentQjRaw";
import axios from 'axios'

export default {
  name: 'Index',
  dicts: ['project_type'],
  components: {},
  data() {
    return {
      // websocket部分
      ws: null, //websocket
      wsUrl: 'ws://127.0.0.1:7070/prod-api/websocket/message',
      // wsUrl: 'ws://192.168.1.2:7070/websocket/message',
      // 连接标识，避免重复连接
      isConnect: false,
      // 短线重连后，延迟5秒重新创建WebSocket连接，rec用来存储延迟请求的代码
      rec: null,
      // WebSocket心跳发送/返回的信息
      checkMsg: {hmsg: 'heartbeat'},
      // 每段时间发送一次心跳包,这里为20秒
      timeout: 20000,
      // 延时发送消息对象(启动心跳新建这个对象，收到消息后重置对象)
      timeoutObj: null,

      projectOptions: [],
      projectId: null,
      projectInfo: {},
      eqmtByTypes: [],
      alarmStatusData: {
        hasAlarm: false,
        items: [],
        maxLevel: 0
      },
      shiftData: [],
      rainData: [],
      fissureData: [],
      dipAngleData: [],
      weeks: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
      geoCoordMap: {
        '海门': [121.15, 31.89],
        '鄂尔多斯': [109.781327, 39.608266],
        '招远': [120.38, 37.35],
        '舟山': [122.207216, 29.985295],
        '齐齐哈尔': [123.97, 47.33],
        '盐城': [120.13, 33.38],
        '赤峰': [118.87, 42.28],
        '青岛': [120.33, 36.07],
        '乳山': [121.52, 36.89],
        '金昌': [102.188043, 38.520089],
        '泉州': [118.58, 24.93],
        '莱西': [120.53, 36.86],
        '日照': [119.46, 35.42],
        '胶南': [119.97, 35.88],
        '南通': [121.05, 32.08],
        '拉萨': [91.11, 29.97],
        '云浮': [112.02, 22.93],
        '梅州': [116.1, 24.55],
        '文登': [122.05, 37.2],
        '上海': [121.48, 31.22],
        '攀枝花': [101.718637, 26.582347],
        '威海': [122.1, 37.5],
        '承德': [117.93, 40.97],
        '厦门': [118.1, 24.46],
        '汕尾': [115.375279, 22.786211],
        '潮州': [116.63, 23.68],
        '丹东': [124.37, 40.13],
        '太仓': [121.1, 31.45],
        '曲靖': [103.79, 25.51],
        '烟台': [121.39, 37.52],
        '福州': [119.3, 26.08],
        '瓦房店': [121.979603, 39.627114],
        '即墨': [120.45, 36.38],
        '抚顺': [123.97, 41.97],
        '玉溪': [102.52, 24.35],
        '张家口': [114.87, 40.82],
        '阳泉': [113.57, 37.85],
        '莱州': [119.942327, 37.177017],
        '湖州': [120.1, 30.86],
        '汕头': [116.69, 23.39],
        '昆山': [120.95, 31.39],
        '宁波': [121.56, 29.86],
        '湛江': [110.359377, 21.270708],
        '揭阳': [116.35, 23.55],
        '荣成': [122.41, 37.16],
        '连云港': [119.16, 34.59],
        '葫芦岛': [120.836932, 40.711052],
        '常熟': [120.74, 31.64],
        '东莞': [113.75, 23.04],
        '河源': [114.68, 23.73],
        '淮安': [119.15, 33.5],
        '泰州': [119.9, 32.49],
        '南宁': [108.33, 22.84],
        '营口': [122.18, 40.65],
        '惠州': [114.4, 23.09],
        '江阴': [120.26, 31.91],
        '蓬莱': [120.75, 37.8],
        '韶关': [113.62, 24.84],
        '嘉峪关': [98.289152, 39.77313],
        '广州': [113.23, 23.16],
        '延安': [109.47, 36.6],
        '太原': [112.53, 37.87],
        '清远': [113.01, 23.7],
        '中山': [113.38, 22.52],
        '昆明': [102.73, 25.04],
        '寿光': [118.73, 36.86],
        '盘锦': [122.070714, 41.119997],
        '长治': [113.08, 36.18],
        '深圳': [114.07, 22.62],
        '珠海': [113.52, 22.3],
        '宿迁': [118.3, 33.96],
        '咸阳': [108.72, 34.36],
        '铜川': [109.11, 35.09],
        '平度': [119.97, 36.77],
        '佛山': [113.11, 23.05],
        '海口': [110.35, 20.02],
        '江门': [113.06, 22.61],
        '章丘': [117.53, 36.72],
        '肇庆': [112.44, 23.05],
        '大连': [121.62, 38.92],
        '临汾': [111.5, 36.08],
        '吴江': [120.63, 31.16],
        '石嘴山': [106.39, 39.04],
        '沈阳': [123.38, 41.8],
        '苏州': [120.62, 31.32],
        '茂名': [110.88, 21.68],
        '嘉兴': [120.76, 30.77],
        '长春': [125.35, 43.88],
        '胶州': [120.03336, 36.264622],
        '银川': [106.27, 38.47],
        '张家港': [120.555821, 31.875428],
        '三门峡': [111.19, 34.76],
        '锦州': [121.15, 41.13],
        '南昌': [115.89, 28.68],
        '柳州': [109.4, 24.33],
        '三亚': [109.511909, 18.252847],
        '自贡': [104.778442, 29.33903],
        '吉林': [126.57, 43.87],
        '阳江': [111.95, 21.85],
        '泸州': [105.39, 28.91],
        '西宁': [101.74, 36.56],
        '宜宾': [104.56, 29.77],
        '呼和浩特': [111.65, 40.82],
        '成都': [104.06, 30.67],
        '大同': [113.3, 40.12],
        '镇江': [119.44, 32.2],
        '桂林': [110.28, 25.29],
        '张家界': [110.479191, 29.117096],
        '宜兴': [119.82, 31.36],
        '北海': [109.12, 21.49],
        '西安': [108.95, 34.27],
        '金坛': [119.56, 31.74],
        '东营': [118.49, 37.46],
        '牡丹江': [129.58, 44.6],
        '遵义': [106.9, 27.7],
        '绍兴': [120.58, 30.01],
        '扬州': [119.42, 32.39],
        '常州': [119.95, 31.79],
        '潍坊': [119.1, 36.62],
        '重庆': [106.54, 29.59],
        '台州': [121.420757, 28.656386],
        '南京': [118.78, 32.04],
        '滨州': [118.03, 37.36],
        '贵阳': [106.71, 26.57],
        '无锡': [120.29, 31.59],
        '本溪': [123.73, 41.3],
        '克拉玛依': [84.77, 45.59],
        '渭南': [109.5, 34.52],
        '马鞍山': [118.48, 31.56],
        '宝鸡': [107.15, 34.38],
        '焦作': [113.21, 35.24],
        '句容': [119.16, 31.95],
        '北京': [116.46, 39.92],
        '徐州': [117.2, 34.26],
        '衡水': [115.72, 37.72],
        '包头': [110, 40.58],
        '绵阳': [104.73, 31.48],
        '乌鲁木齐': [87.68, 43.77],
        '枣庄': [117.57, 34.86],
        '杭州': [120.19, 30.26],
        '淄博': [118.05, 36.78],
        '鞍山': [122.85, 41.12],
        '溧阳': [119.48, 31.43],
        '库尔勒': [86.06, 41.68],
        '安阳': [114.35, 36.1],
        '开封': [114.35, 34.79],
        '济南': [117, 36.65],
        '德阳': [104.37, 31.13],
        '温州': [120.65, 28.01],
        '九江': [115.97, 29.71],
        '邯郸': [114.47, 36.6],
        '临安': [119.72, 30.23],
        '兰州': [103.73, 36.03],
        '沧州': [116.83, 38.33],
        '临沂': [118.35, 35.05],
        '南充': [106.110698, 30.837793],
        '天津': [117.2, 39.13],
        '富阳': [119.95, 30.07],
        '泰安': [117.13, 36.18],
        '诸暨': [120.23, 29.71],
        '郑州': [113.65, 34.76],
        '哈尔滨': [126.63, 45.75],
        '聊城': [115.97, 36.45],
        '芜湖': [118.38, 31.33],
        '唐山': [118.02, 39.63],
        '平顶山': [113.29, 33.75],
        '邢台': [114.48, 37.05],
        '德州': [116.29, 37.45],
        '济宁': [116.59, 35.38],
        '荆州': [112.239741, 30.335165],
        '宜昌': [111.3, 30.7],
        '义乌': [120.06, 29.32],
        '丽水': [119.92, 28.45],
        '洛阳': [112.44, 34.7],
        '秦皇岛': [119.57, 39.95],
        '株洲': [113.16, 27.83],
        '石家庄': [114.48, 38.03],
        '莱芜': [117.67, 36.19],
        '常德': [111.69, 29.05],
        '保定': [115.48, 38.85],
        '湘潭': [112.91, 27.87],
        '金华': [119.64, 29.12],
        '岳阳': [113.09, 29.37],
        '长沙': [113, 28.21],
        '衢州': [118.88, 28.97],
        '廊坊': [116.7, 39.53],
        '菏泽': [115.480656, 35.23375],
        '合肥': [117.27, 31.86],
        '武汉': [114.31, 30.52],
        '大庆': [125.03, 46.58]
      },
      nowTime: '',
      timer: null,
      // 天气信息
      weatherInfo: {
        temperature: '--',
        type: '未知',
        windDirection: '--',
        windPower: '--',
        humidity: '--',
        updateTime: '--'
      },
    }
  },
  created() {
    this.getProjects();
  },
  mounted() {
    this.$nextTick(() => {
      this.initWebSocket();

      this.getTimer();
      this.placeholderPic();
      rainBg();
      dashed();
      this.startWeatherRefresh();

      // 确保容器已经渲染完成
      setTimeout(() => {
        this.getWyChartData(this.projectId);
      }, 200);
    });

  },
  destroyed() {
    if(this.timer)
      clearInterval(this.timer);
  },
  methods: {
    initWebSocket() {
      let that = this;
      that.ws = new WebSocket(that.wsUrl);
      that.ws.onopen = that.webSocketOnOpen;
      that.ws.onmessage = that.webSocketOnMessage;
      //当websocket因为各种原因（正常或者异常）关闭之后，会调用onclose方法
      that.ws.onclose = that.webSocketClose;
      //当websocket因为异常原因（比如服务器部署、断网等）关闭之后，会调用onerror方法
      //在onerror中需要调用reConnect方法重连服务器
      that.ws.onerror = that.webSocketOnError;
    },
    webSocketOnOpen() {
      let that = this;
      //建立连接后开始心跳
      //因为nginx一般回设置例如60s没有传输数据就断开连接，所以要定时发送数据
      that.timeoutObj = setTimeout(function () {
        if (that.isConnect)
          that.ws.send(that.checkMsg);
      }, that.timeout);
    },
    webSocketOnError() {
      let that = this;
      //连接断开后修改标识
      that.isConnect = false;
      //连接错误，需要重新连接
      that.reConnect();
    },
    webSocketOnMessage(e) {
      //数据接收
      if (e.data && e.data.indexOf('{') === 0) {
        var jsonData = JSON.parse(e.data);
        if (jsonData) {
          switch (jsonData.type) {
            case 1:// 更新设备状态
              this.getEqmtGroupTypes(this.projectId);
              break;
            case 2:// 更新位移信息
              if(jsonData.message && jsonData.message.indexOf('{') === 0){
                var rawJson = JSON.parse(jsonData.message);
                if(this.shiftData){
                  for(let it = 0; it < this.shiftData.length; it++){
                    if(this.shiftData[it].eqmtId === rawJson.eqmtId){
                      this.shiftData[it].valueWy = rawJson.value;
                    }
                  }
                  this.createWyChart();
                }
                if(this.alarmStatusData.items && this.alarmStatusData.items.length > 0){
                  let hasEqmtType = false;
                  for(let it1 = 0; it1 < this.alarmStatusData.items.length; it1++){
                    if(this.alarmStatusData.items[it1].eqmtTypeId === rawJson.eqmtTypeId){
                      this.alarmStatusData.items[it1].maxAlarmLevel = rawJson.alarmLevel;
                      hasEqmtType = true;
                      break;
                    }
                  }
                  if(!hasEqmtType){
                    this.alarmStatusData.items.push({
                      eqmtTypeId: rawJson.eqmtTypeId,
                      eqmtTypeName: rawJson.eqmtTypeName,
                      maxAlarmLevel: rawJson.alarmLevel
                    });
                  }
                  let maxLevel = 100;
                  for(let it1 = 0; it1 < this.alarmStatusData.items.length; it1++){
                    if(this.alarmStatusData.items[it1].maxAlarmLevel < maxLevel){
                      maxLevel = this.alarmStatusData.items[it1].maxAlarmLevel;
                    }
                  }
                  this.alarmStatusData.maxLevel = maxLevel;
                } else if(rawJson.alarmLevel > 0){
                  this.alarmStatusData.items.push({
                    eqmtTypeId: rawJson.eqmtTypeId,
                    eqmtTypeName: rawJson.eqmtTypeName,
                    maxAlarmLevel: rawJson.alarmLevel
                  });
                  this.alarmStatusData.hasAlarm = true;
                  this.alarmStatusData.maxLevel = rawJson.alarmLevel;
                }
              }
              break;
            case 3:// 更新裂缝信息
              this.getEqmtAlarmStatus(this.projectId);
              this.getLfChartData(this.projectId);
              break;
            case 4:// 更新倾角信息
              this.getEqmtAlarmStatus(this.projectId);
              this.getQjChartData(this.projectId);
              break;
            case 5:// 更新雨量信息
              this.getEqmtAlarmStatus(this.projectId);
              this.getYlChartData(this.projectId);
              break;
            case 6:// 更新报警信息
              this.getEqmtAlarmStatus(this.projectId);
              break;
          }
        }
      }
    },
    webSocketClose(e) {
      let that = this;
      //连接断开后修改标识
      that.isConnect = false;
      //连接错误，需要重连
      that.reConnect();
    },
    reConnect() {
      let that = this;
      //如果已经脸上就不再重连
      if (that.isConnect) {
        return;
      }
      clearTimeout(that.rec);
      //延迟5秒重连，避免频繁请求重连
      that.rec = setTimeout(function () {
        that.initWebSocket();
      }, 5000);
    },

    getProjects() {
      listProject().then(response => {
        this.projectOptions = response.rows;
        if (response.rows)
          this.projectId = response.rows[0].id
        else
          this.projectId = null;

        this.getProjectInfo(this.projectId);
        this.getEqmtTypes(this.projectId);
        this.getEqmtGroupTypes(this.projectId);
        this.getEqmtAlarmStatus(this.projectId);
        this.getWyChartData(this.projectId);
        this.getYlChartData(this.projectId);
        this.getLfChartData(this.projectId);
        this.getQjChartData(this.projectId);
      });
    },
    getProjectInfo(projectId) {
      getProject(projectId).then(response => {
        this.projectInfo = response.data;
      });
    },
    getEqmtTypes(projectId) {
      listEqmtTypeByProjectId(projectId).then(response => {
        let mapValue = '';
        if (response.items) {
          response.items.forEach(item => {
            mapValue += item + '</br>'
          })
        }
        let data = [];
        data.push({name: this.projectInfo.city, value: mapValue});
        this.map(data);
      });
    },
    map(mapData) {
      var myChart = echarts.init(this.$refs.mapCharts);

      let that = this;
      var convertData = function (data) {
        var res = [];
        if (data) {
          for (var i = 0; i < data.length; i++) {
            var geoCoord = that.geoCoordMap[data[i].name];
            if (geoCoord) {
              res.push({
                name: data[i].name,
                value: geoCoord.concat(data[i].value)
              });
            }
          }
        }
        return res;
      };

      var option = {
        tooltip: {
          trigger: 'item',
          formatter: function (params) {
            var toolTipHtml = params.name + ':<br>';
            if (params.value[2]) {
              toolTipHtml += params.value[2];
            }
            return toolTipHtml;
          }
        },
        geo: {
          map: 'china',
          zoom: 1.2,//放大
          label: {
            emphasis: {
              show: true
            }
          },
          roam: true,
          itemStyle: {
            normal: {
              areaColor: 'rgba(2,37,101,.5)',
              borderColor: 'rgba(112,187,252,.5)'
            },
            emphasis: {
              areaColor: 'rgba(2,37,101,.8)'
            }
          }
        },
        series: [
          {
            name: '安全设备配置',
            type: 'effectScatter',
            coordinateSystem: 'geo',
            rippleEffect: {
              brushType: 'stroke'
            },
            data: convertData(mapData),
            symbolSize: function (val) {
              return 10;
            },
            label: {
              normal: {
                formatter: '{b}',
                position: 'right',
                show: false
              },
              emphasis: {
                show: true
              }
            },
            itemStyle: {
              normal: {
                color: '#ffeb7b'
              }
            }
          }
        ]
      };
      myChart.setOption(option);
      window.addEventListener("resize", function () {
        myChart.resize();
      });
    },
    getEqmtGroupTypes(projectId) {
      listEqmtsGroupByType({projectId: projectId}).then(response => {
        this.eqmtByTypes = response.items;
      });
    },
    getEqmtAlarmStatus(projectId) {
      queryEquipmentAlarmStatus({projectId: projectId}).then(response => {
        this.alarmStatusData.hasAlarm = response.hasAlarm;
        this.alarmStatusData.items = response.items;
        this.alarmStatusData.maxLevel = response.maxLevel;
      });
    },
    getWyChartData(projectId) {
      this.shiftData = [];
      listWyRawForCharts().then(response => {
        if (response.code === 200) {
          this.shiftData = response.items;
          this.createWyChart();
        }
      });
    },
    createWyChart() {
      let legends = ["X位移(mm)", "Y位移(mm)"];
      let xAxisMarks = [];
      let serielXItemData = [];
      let serielYItemData = [];
      if (this.shiftData) {
        for (let it = 0; it < this.shiftData.length; it++) {
          if(this.shiftData[it].xorY === 'X') {
            serielXItemData.push(Math.abs(this.shiftData[it].valueWy));
            xAxisMarks.push(this.shiftData[it].name)
          } else if(this.shiftData[it].xorY === 'Y')
            serielYItemData.push(Math.abs(this.shiftData[it].valueWy));
        }
      }
      this.shiftChart('shiftChart', legends, xAxisMarks, serielXItemData, serielYItemData);
    },
    shiftChart(elem, legends, xAxisMarks, serielXItemData, serielYItemData) {
      // 确保容器存在
      const container = document.getElementById(elem);
      if (!container) {
        console.error('图表容器不存在');
        return;
      }

      // 设置容器样式
      container.style.width = '100%';
      container.style.height = '100%';
      container.style.minHeight = '300px';

      var option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function(params) {
            let result = params[0].name + '<br/>';
            params.forEach(item => {
              result += item.seriesName + ': ' +
                       Math.abs(item.value).toFixed(3) + 'mm<br/>';
            });
            return result;
          }
        },
        legend: {
          data: legends,
          textStyle: {
            color: '#fff'
          },
          top: 10
        },
        grid: {
          top: '15%',
          left: '5%',
          right: '5%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xAxisMarks,
          axisLabel: {
            color: '#fff',
            rotate: 35,
            fontSize: 9,
            interval: 0,
            formatter: function (value) {
              return value.length > 10 ? value.substr(0, 10) + '...' : value;
            }
          },
          axisLine: {
            lineStyle: {
              color: '#57617B'
            }
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            color: '#fff'
          },
          axisLine: {
            lineStyle: {
              color: '#57617B'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#57617B',
              opacity: 0.3
            }
          }
        },
        series: [
          {
            name: 'X位移(mm)',
            type: 'bar',
            barWidth: '25%',
            data: serielXItemData,
            itemStyle: {
              color: '#B996F8'
            },
            emphasis: {
              itemStyle: {
                color: '#9B79F7'
              }
            }
          },
          {
            name: 'Y位移(mm)',
            type: 'bar',
            barWidth: '25%',
            data: serielYItemData,
            itemStyle: {
              color: '#03C2EC'
            },
            emphasis: {
              itemStyle: {
                color: '#02A9CE'
              }
            }
          }
        ]
      };

      // 初始化图表
      let myChart = echarts.init(container);
      myChart.clear();
      myChart.setOption(option);

      // 监听容器大小变化
      const resizeObserver = new ResizeObserver(() => {
        myChart.resize();
      });
      resizeObserver.observe(container);

      // 窗口大小变化时重绘
      window.addEventListener('resize', () => {
        myChart.resize();
      });
    },

    getYlChartData(projectId) {
      this.rainData = [];
      listYlRawForCharts({limit: 10}).then(response => {
        if (response.code === 200) {
          this.rainData = response.items;
          this.createYlChart();
        }
      });
    },
    createYlChart() {
      let serielXItemData = [];
      let xAxisMarks = [];
      if (this.rainData) {
        for (let it = 0; it < this.rainData.length; it++) {
          xAxisMarks.push(this.rainData[it].catchTimeMark)
          serielXItemData.push(this.rainData[it].valueYl);
        }
      }
      this.rainChart('rainChart', xAxisMarks, serielXItemData);
    },
    rainChart(elem, xAxisMarks, serielXItemData) {
      var option = {
        tooltip: {trigger: 'axis',axisPointer: {lineStyle: {color: '#fff'}}},
        // legend: {
        //   icon: 'rect',
        //   itemWidth: 14,itemHeight: 5,itemGap:10,
        //   data: ["雨量(mm)"],
        //   right: '10px',top: '0px',
        //   textStyle: {fontSize: 12,color: '#fff'}
        // },
        grid: {x:40,y:30,x2:45,y2:50},
        xAxis: [{
          type: 'category',boundaryGap: false,axisLine: {lineStyle: {color: '#57617B'}},axisLabel: {textStyle: {color:'#fff'}, rotate: 35, fontSize: 9},
          data: xAxisMarks
        }],
        yAxis: [{
          type: 'value',
          axisTick: {
            show: false
          },
          axisLine: {lineStyle: {color: '#57617B'}},
          axisLabel: {margin: 10,textStyle: {fontSize: 12},textStyle: {color:'#fff'},formatter:'{value}'},
          splitLine: {lineStyle: {color: '#57617B'}}
        }],
        series: [{
          name: '雨量(mm)',type: 'line',smooth: true,lineStyle: {normal: {width: 1}},
          yAxisIndex:0,
          areaStyle: {
            normal: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                offset: 0,
                color: 'rgba(185,150,248,0.3)'
              }, {
                offset: 0.8,
                color: 'rgba(185,150,248,0)'
              }], false),
              shadowColor: 'rgba(0, 0, 0, 0.1)',
              shadowBlur: 10
            }
          },
          itemStyle: {normal: { color: '#B996F8'}},
          data: serielXItemData,
        }]
      };
      var myChart = echarts.init(document.getElementById(elem));
      myChart.clear();
      myChart.setOption(option);
    },

    getLfChartData(projectId) {
      this.fissureData = [];
      listLfRawForCharts({projectId: projectId, eqmtTypeSymbol: 'LF', limit: 10}).then(response => {
        if (response.code === 200) {
          this.fissureData = response.items;
          this.createLfChart();
        }
      });
    },
    createLfChart() {
      let serielXItemData = [];
      let xAxisMarks = [];
      if (this.fissureData) {
        for (let it = 0; it < this.fissureData.length; it++) {
          xAxisMarks.push(this.fissureData[it].name)
          serielXItemData.push(this.fissureData[it].valueLf);
        }
      }
      this.fissureChart('fissureChart', xAxisMarks, serielXItemData);
    },
    fissureChart(elem, xAxisMarks, serielXItemData) {
      var option = {
        tooltip: {trigger: 'axis',axisPointer: {lineStyle: {color: '#fff'}}},
        // legend: {
        //   icon: 'rect',
        //   itemWidth: 14,itemHeight: 5,itemGap:10,
        //   data: ["裂缝(mm)"],
        //   right: '10px',top: '0px',
        //   textStyle: {fontSize: 12,color: '#fff'}
        // },
        grid: {x:40,y:30,x2:45,y2:40},
        xAxis: [{
          type: 'category',boundaryGap: false,axisLine: {lineStyle: {color: '#57617B'}},axisLabel: {textStyle: {color:'#fff'}, rotate: 35, fontSize: 10 },
          data: xAxisMarks
        }],
        yAxis: [{
          type: 'value',
          axisTick: {
            show: false
          },
          axisLine: {lineStyle: {color: '#57617B'}},
          axisLabel: {margin: 10,textStyle: {fontSize: 12},textStyle: {color:'#fff'},formatter:'{value}'},
          splitLine: {lineStyle: {color: '#57617B'}}
        }],
        series: [{
          name: '裂缝(mm)',
          type: 'bar',
          barWidth: '40%',
          barGap: '30%',
          smooth: true,
          yAxisIndex:0,
          areaStyle: {
            normal: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                offset: 0,
                color: 'rgba(185,150,248,0.3)'
              }, {
                offset: 0.8,
                color: 'rgba(185,150,248,0)'
              }], false),
              shadowColor: 'rgba(0, 0, 0, 0.1)',
              shadowBlur: 10
            }
          },
          itemStyle: {normal: { color: '#B996F8'}},
          data: serielXItemData
        }]
      };
      var myChart = echarts.init(document.getElementById(elem));
      myChart.clear();
      myChart.setOption(option);
    },

    getQjChartData(projectId) {
      this.dipAngleData = [];
      listQjRawForCharts({projectId: projectId, eqmtTypeSymbol: 'QJ', limit: 10}).then(response => {
        if (response.code === 200) {
          this.dipAngleData = response.items;
          this.createQjChart();
        }
      });
    },
    createQjChart() {
      let legends = ["X倾角(°)", "Y倾角(°)", "Z倾角(°)"];
      let xAxisMarks = [];
      let serielXItemData = [];
      let serielYItemData = [];
      let serielZItemData = [];
      if (this.dipAngleData) {
        for (let it = 0; it < this.dipAngleData.length; it++) {
          xAxisMarks.push(this.dipAngleData[it].name)
          serielXItemData.push(this.dipAngleData[it].valueQjX);
          serielYItemData.push(this.dipAngleData[it].valueQjY);
          serielZItemData.push(this.dipAngleData[it].valueQjZ);
        }
      }
      this.dipAngleChart('dipAngleChart', legends, xAxisMarks, serielXItemData, serielYItemData, serielZItemData);
    },
    dipAngleChart(elem, legends, xAxisMarks, serielXItemData, serielYItemData, serielZItemData) {
      var option = {
        tooltip: {trigger: 'axis', axisPointer: {lineStyle: {color: '#fff'}}},
        legend: {
          icon: 'rect',
          itemWidth: 14,
          itemHeight: 5,
          itemGap: 10,
          data: legends,
          right: '10px',
          top: '0px',
          textStyle: {fontSize: 12, color: '#fff'},
          selected: {
            'X倾角(°)': false,  // 默认不显示X倾角
            'Y倾角(°)': true,   // 默认显示Y倾角
            'Z倾角(°)': false   // 默认不显示Z倾角
          }
        },
        grid: {x:40, y:30, x2:45, y2:40},
        xAxis: [{
          type: 'category',
          boundaryGap: false,
          axisLine: {lineStyle: {color: '#57617B'}},
          axisLabel: {textStyle: {color:'#fff'}, rotate: 35, fontSize: 10},
          data: xAxisMarks
        }],
        yAxis: [{
          type: 'value',
          axisTick: {show: false},
          axisLine: {lineStyle: {color: '#57617B'}},
          axisLabel: {margin: 10, textStyle: {fontSize: 12, color:'#fff'}, formatter:'{value}'},
          splitLine: {lineStyle: {color: '#57617B'}}
        }],
        series: [
          {
            name: 'X倾角(°)',
            type: 'bar',
            barWidth: '20%',
            barGap: '30%',
            smooth: true,
            yAxisIndex: 0,
            areaStyle: {
              normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                  offset: 0,
                  color: 'rgba(185,150,248,0.3)'
                }, {
                  offset: 0.8,
                  color: 'rgba(185,150,248,0)'
                }], false),
                shadowColor: 'rgba(0, 0, 0, 0.1)',
                shadowBlur: 10
              }
            },
            itemStyle: {normal: {color: '#B996F8'}},
            data: serielXItemData
          },
          {
            name: 'Y倾角(°)',
            type: 'bar',
            barWidth: '20%',
            barGap: '30%',
            smooth: true,
            yAxisIndex: 0,
            areaStyle: {
              normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                  offset: 0,
                  color: 'rgba(3, 194, 236, 0.3)'
                }, {
                  offset: 0.8,
                  color: 'rgba(3, 194, 236, 0)'
                }], false),
                shadowColor: 'rgba(0, 0, 0, 0.1)',
                shadowBlur: 10
              }
            },
            itemStyle: {normal: {color: '#03C2EC'}},
            data: serielYItemData
          },
          {
            name: 'Z倾角(°)',
            type: 'bar',
            barWidth: '20%',
            barGap: '30%',
            smooth: true,
            yAxisIndex: 0,
            areaStyle: {
              normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                  offset: 0,
                  color: 'rgba(3,236,77,0.3)'
                }, {
                  offset: 0.8,
                  color: 'rgba(3, 194, 236, 0)'
                }], false),
                shadowColor: 'rgba(0, 0, 0, 0.1)',
                shadowBlur: 10
              }
            },
            itemStyle: {normal: {color: '#03ec4d'}},
            data: serielZItemData
          }
        ]
      };

      var myChart = echarts.init(document.getElementById(elem));
      myChart.clear();
      myChart.setOption(option);
    },

    placeholderPic() {
      var w = document.documentElement.clientWidth / 80;
      document.documentElement.style.fontSize = w + 'px';
    },
    getTimer() {
      this.timer = setInterval(() => {
        var date = moment().format("YYYY/MM/DD HH:mm:ss");
        var day = moment().format('d');
        this.nowTime = date + ' ' + this.weeks[day];
      }, 1000);
    },
    noDataTip($selector) {
      var currModH = $selector.height();
      var top = currModH > 180 ? "35%" : "13%";
      var $li = [
        "<div class='no-data' style='width:90%;position: absolute;top:" + top + ";text-align:center;color:#a59999;'>",
        "<div style='font-size:16px;opacity:0.7;color:#fff;'>暂无数据</div>",
        "</div>"
      ]
      $selector.append($li.join(""));
    },

    // 定时刷新天气
    startWeatherRefresh() {
      // 立即获取一次天气
      this.getWeather()
      // 每30分钟更新一次天气信息
      setInterval(() => {
        this.getWeather()
      }, 30 * 60 * 1000)
    },

    // 获取天气信息
    async getWeather() {
      try {
        // 使用高德地图天气API
        const response = await axios.get('https://restapi.amap.com/v3/weather/weatherInfo', {
          params: {
            key: 'c402322dfb9adadda3c73d2211f2a22f', // 替换成您的高德Web服务API密钥
            city: '340500', // 马鞍山市的行政区划代码
            extensions: 'base'
          }
        })

        if (response.data.status === '1' && response.data.lives?.length > 0) {
          const weatherData = response.data.lives[0]
          this.weatherInfo = {
            temperature: weatherData.temperature,
            type: weatherData.weather,
            windDirection: weatherData.winddirection,
            windPower: weatherData.windpower + '级',
            humidity: weatherData.humidity,
            updateTime: weatherData.reporttime.split(' ')[1]
          }
        }
      } catch (error) {
        console.error('获取天气信息失败:', error)
      }
    },

    // 获取天气图标
    getWeatherIcon(type) {
      // 气类型映射到图标
      const iconMap = {
        '晴': 'el-icon-sunny',
        '多云': 'el-icon-cloudy',
        '阴': 'el-icon-partly-cloudy',
        '小雨': 'el-icon-light-rain',
        '中雨': 'el-icon-moderate-rain',
        '大雨': 'el-icon-heavy-rain',
        '雷阵雨': 'el-icon-storm-rain',
        '阵雨': 'el-icon-shower-rain',
        '雪': 'el-icon-snow',
        '雾': 'el-icon-fog',
        '霾': 'el-icon-haze'
      }

      // 处理天气类型中包含特定关键的情况
      const weatherType = type.toLowerCase()
      if (weatherType.includes('雨')) return 'el-icon-heavy-rain'
      if (weatherType.includes('云')) return 'el-icon-cloudy'
      if (weatherType.includes('晴')) return 'el-icon-sunny'
      if (weatherType.includes('阴')) return 'el-icon-partly-cloudy'
      if (weatherType.includes('雪')) return 'el-icon-snow'
      if (weatherType.includes('雾')) return 'el-icon-fog'
      if (weatherType.includes('霾')) return 'el-icon-haze'

      // 返回映射的图标或默认图标
      return iconMap[type] || 'el-icon-sunny'
    },
  }
}
</script>

<style lang="scss">
.shift {
  width: 100%;
  height: 100%;
  position: relative;
}

.shift-content {
  width: 100%;
  height: calc(100% - 40px); /* 减去标题高度 */
  position: relative;
}

.shiftChart {
  width: 100%;
  height: 100%;
  min-height: 300px;
}

.sphere {
  .sum {
    .alarm-p {
      display: flex;
      flex-wrap: wrap; // 允许在需要时换行
      gap: 8px;
      justify-content: center; // 水平居中
      align-items: center; // 垂直居中

      .alarm-item {
        display: inline-flex;
        align-items: center;
        white-space: nowrap; // 保持文本在一行

        .warning-icon {
          margin-right: 4px;
        }
      }
    }

    .normal-p {
      display: flex;
      align-items: center;
      justify-content: center;
      color: #00FF7F;
      font-size: 23px;
      font-weight: 600;
      text-shadow: 0 0 8px rgba(0, 255, 127, 0.6);
      letter-spacing: 1px;
padding-left: 20px;
      .el-icon-success {
        position: relative;
        top: 1px;
        font-size: 23px;
      }
    }
  }
}

// 报警颜色调整
.spanColor1 { // 1级报警 - 最严重 - 鲜红色
  color: #FF4444;
  .warning-icon { color: #FF4444; }
}
.spanColor2 { // 2级报警 - 中等 - 亮橙色
  color: #FF8800;
  .warning-icon { color: #FF8800; }
}
.spanColor3 { // 3级报警 - 最轻 - 亮黄色
  color: #FFDD00;
  .warning-icon { color: #FFDD00; }
}

// 报警动画加强
.alarmSpeed1 {
  animation: blink 0.8s ease-in-out infinite;
  text-shadow: 0 0 10px rgba(255, 68, 68, 0.8);
}
.alarmSpeed2 {
  animation: blink 1.2s ease-in-out infinite;
  text-shadow: 0 0 10px rgba(255, 136, 0, 0.8);
}
.alarmSpeed3 {
  animation: blink 1.6s ease-in-out infinite;
  text-shadow: 0 0 10px rgba(255, 221, 0, 0.8);
}

@keyframes blink {
  0% { opacity: 1; }
  50% { opacity: 0.6; }
  100% { opacity: 1; }
}
</style>
