<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="grid-top">
          <h3>快捷菜单</h3>
          <ul>
            <li>
              <img src="@/assets/images/jcdm.svg">
              <p>监测断面</p>
            </li>
            <li>
              <img src="@/assets/images/jcdm.svg">
              <p>设备管理</p>
            </li>
            <li>
              <img src="@/assets/images/jcdm.svg">
              <p>监控设备</p>
            </li>
            <li>
              <img src="@/assets/images/jcdm.svg">
              <p>设备资料</p>
            </li>
            <li>
              <img src="@/assets/images/jcdm.svg">
              <p>报警等级</p>
            </li>
            <li>
              <img src="@/assets/images/jcdm.svg">
              <p>实时数据</p>
            </li>
            <li>
              <a href="/screen" target="_blank">
                <img src="@/assets/images/jcdm.svg">
                <p>大屏</p>
              </a>
            </li>
          </ul>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="grid-right">
          <h3>项目信息</h3>
          <div class="main-info">
            <p>{{projectInfo.projectDesc}}</p>
            <div><span>所属企业：</span>
              <p>{{projectInfo.companyName}}</p></div>
            <div><span>项目类型：</span>
              <p><dict-tag :options="dict.type.project_type" :value="projectInfo.projectType"/></p></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <div class="first-top">
      <ul>
        <li v-for="(item, index) in eqmtByTypes">
          <p>{{item.eqmtTypeName}}设备</p>
          <img src="@/assets/images/jcdm.svg">
          <h1>{{item.onlineCount}}/{{item.totalCount}}</h1>
        </li>
      </ul>

      <div class="first-main">
        <div class="left">
          <el-table v-loading="loading" :data="dataList" border style="height: 520px;overflow: auto;"
                    :header-cell-style="{color:'#000000', fontFamily:'Helvetica',fontSize:'15px'}"
                    :cell-style="{color:'#000000', fontFamily:'Helvetica',fontSize:'15px'}">
            <el-table-column label="设备类型" align="center" prop="eqmtTypeName"/>
            <el-table-column label="设备名称" align="center" prop="eqmtName"/>
            <el-table-column label="报警等级" align="center" prop="alarmLevel"/>
            <el-table-column label="在线状态" align="center" prop="onlineStatus">
              <template slot-scope="scope">
                {{scope.row.onlineStatus != null ? scope.row.onlineStatus === 0 ? "在线" : '离线' : ''}}
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="right">
          <h1>AR全景摄像头</h1>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import {getProject, listProject} from "@/api/aqy/project";
import {listEqmtTypeByProjectId} from "@/api/aqy/aqySectionEqmt";
import {listAqyEquipment, listEqmtsGroupByType, selectAqyEquipmentListForReport} from "@/api/aqy/aqyEquipment";

export default {
  name: "Index",
  dicts: ['project_type'],
  data() {
    return {
      // 版本号
      version: "3.8.8",
      // 遮罩层
      loading: false,
      projectOptions: [],
      projectId: null,
      projectInfo: {},
      eqmtByTypes: [],
      eqmtImages: ["jcdm.svg", "ylj.svg", "lfj.svg", "qjj.svg"],
      // 表格数据
      dataList: [],

      // websocket部分
      ws: null, //websocket
      wsUrl: 'ws://localhost:7070/prod-api/websocket/message',
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
    };
  },
  created() {
    this.getProjects();
  },
  mounted() {
    this.initWebSocket();
  },
  destroyed() {
  },
  methods: {
    getProjects() {
      listProject().then(response => {
        this.projectOptions = response.rows;
        if (response.rows)
          this.projectId = response.rows[0].id
        else
          this.projectId = null;

        this.getProjectInfo(this.projectId);
        this.getEqmtGroupTypes(this.projectId);
        this.getEqmtList(this.projectId);
      });
    },
    getProjectInfo(projectId){
      getProject(projectId).then(response => {
        this.projectInfo = response.data;
      });
    },
    getEqmtGroupTypes(projectId){
      listEqmtsGroupByType({projectId: projectId}).then(response => {
        this.eqmtByTypes = response.items;
      });
    },
    getEqmtList(projectId){
      selectAqyEquipmentListForReport({projectId: projectId}).then(response => {
        this.dataList = response.items;
      });
    },
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
            case 1:// 更新设备状态信息
              this.getEqmtGroupTypes(this.projectId);
              this.getEqmtList(this.projectId);
              break;
            case 2:// 更新设备状态信息
            case 3:// 更新设备状态信息
            case 4:// 更新设备状态信息
            case 5:// 更新设备状态信息
              this.getEqmtList(this.projectId);
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
  }
};
</script>

<style scoped lang="scss">
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.app-container {
  background: #f5f5f5;
  padding: 20px;
}

.grid-top {
  height: 150px;
  background: #FFFFFF;
  border-radius: 6px;
}

h3 {
  padding-left: 15px;
  padding-top: 16px;
  font-weight: bold;
}

ul {
  display: flex;
  justify-content: space-between;
  list-style: none;

}

.grid-top ul li {
  text-align: center;
  margin: 15px 29px;
  cursor: pointer;
}

.grid-top ul li img {
  width: 30px;
  height: 30px;

}

.grid-top ul li p {
  padding-top: 10px;

}

.grid-right {
  height: 150px;
  background: #FFFFFF;
  border-radius: 6px;
}

.grid-right .main-info {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  margin-top: 10px;
  margin-left: 40px;
}

.main-info div {
  display: flex;
  line-height: 40px;
  margin-right: 30px;

}

.main-info span {
  font-size: 16px;
  color: #717780;
}

.main-info p {
  font-size: 16px;
  color: #121315;
}

.first-top {
  height: 650px;
  margin-top: 16px;
  background: #FFFFFF;
  border-radius: 6px;

}

.first-top ul {
  display: flex;
  justify-content: space-around;
  padding: 11px 16px 0 16px;
}

.first-top ul li {
  position: relative;
  width: 215px;
  height: 95px;
  background: #f9faff;
  border-radius: 6px;

}

.first-top ul li p {
  padding-left: 17px;
  padding-top: 22px;
  font-size: 10px;
  color: #888888;
}

.first-top ul li img {
  position: absolute;
  top: 22px;
  right: 16px;
  width: 25px;
  height: 25px;
}

h1 {
  padding-left: 17px;
  padding-top: 11px;
}

.first-main {
  display: flex;
  margin-top: 16px;
}

.first-main .left {
  flex: 1;
  padding-left: 17px;
  margin-right: 30px;

}

.first-main .right {
  display: flex;
  width: 400px;
  justify-content: center;
  align-items: center;
  background: #c4c4c4c4;
}
</style>

