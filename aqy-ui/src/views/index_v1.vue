<template>
  <div class="app-container">
    <!-- 顶部数据概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6" v-for="(item, index) in eqmtByTypes" :key="index">
        <div class="data-card" :class="`card-${index + 1}`">
          <div class="card-content">
            <div class="card-icon">
              <i :class="getCardIcon(item.eqmtTypeName)"></i>
            </div>
            <div class="card-info">
              <h4>{{item.eqmtTypeName}}设备</h4>
              <div class="card-numbers">
                <span class="online">{{item.onlineCount}}</span>
                <span class="divider">/</span>
                <span class="total">{{item.totalCount}}</span>
              </div>
              <div class="status-bar">
                <div class="progress" :style="{width: (item.onlineCount/item.totalCount*100) + '%'}"></div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 项目信息与快捷菜单 -->
    <el-row :gutter="20" class="middle-section">
      <el-col :span="16">
        <div class="project-info-card">
          <div class="card-header">
            <h3>项目概况</h3>
            <el-tag size="small" :type="getProjectTypeTag(projectInfo.projectType)">
              {{dict.type.project_type[projectInfo.projectType]}}
            </el-tag>
          </div>
          <div class="project-details">
            <p class="project-desc">{{projectInfo.projectDesc}}</p>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">所属企业</span>
                <span class="value">{{projectInfo.companyName}}</span>
              </div>
              <div class="info-item">
                <span class="label">项目状态</span>
                <el-tag size="mini" type="success">运行中</el-tag>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="quick-menu">
          <h3>快捷操作</h3>
          <div class="menu-grid">
            <div class="menu-item" v-for="(menu, index) in quickMenus" :key="index" @click="handleMenuClick(menu)">
              <i :class="menu.icon"></i>
              <span>{{menu.name}}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 设备状态表格与实时监控 -->
    <el-row :gutter="20" class="bottom-section">
      <el-col :span="16">
        <div class="device-table-card">
          <div class="card-header">
            <h3>设备状态</h3>
            <el-button type="text" @click="refreshData">
              <i class="el-icon-refresh"></i> 刷新
            </el-button>
          </div>
          <el-table
            v-loading="loading"
            :data="dataList"
            border
            style="width: 100%"
            :header-cell-style="tableHeaderStyle"
            :cell-style="tableCellStyle">
            <el-table-column label="设备类型" align="center" prop="eqmtTypeName">
              <template slot-scope="scope">
                <div class="device-type">
                  <i :class="getDeviceIcon(scope.row.eqmtTypeName)"></i>
                  <span>{{scope.row.eqmtTypeName}}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="设备名称" align="center" prop="eqmtName"/>
            <el-table-column label="报警等级" align="center" prop="alarmLevel">
              <template slot-scope="scope">
                <el-tag :type="getAlarmLevelType(scope.row.alarmLevel)">
                  {{scope.row.alarmLevel}}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="在线状态" align="center" prop="onlineStatus">
              <template slot-scope="scope">
                <el-tag :type="scope.row.onlineStatus === 0 ? 'success' : 'info'">
                  {{formatOnlineStatus(scope.row.onlineStatus)}}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="monitor-card">
          <div class="card-header">
            <h3>实时监控</h3>
            <el-dropdown>
              <span class="el-dropdown-link">
                切换视角<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>视角1</el-dropdown-item>
                <el-dropdown-item>视角2</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div class="monitor-content">
            <div class="camera-view">
              <img src="" alt="监控画面">
              <div class="camera-overlay">
                <span class="status-badge">
                  <i class="el-icon-video-camera"></i> 实时
                </span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
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
      quickMenus: [
        {
          name: '监测断面',
          icon: 'el-icon-view',
          path: '/aqy/section'
        },
        {
          name: '设备管理',
          icon: 'el-icon-setting',
          path: '/aqy/equipment'
        },
        {
          name: '监控设备',
          icon: 'el-icon-monitor',
          path: '/aqy/monitor'
        },
        {
          name: '设备资料',
          icon: 'el-icon-document',
          path: '/aqy/document'
        },
        {
          name: '报警等级',
          icon: 'el-icon-warning',
          path: '/aqy/alarm'
        },
        {
          name: '实时数据',
          icon: 'el-icon-data-line',
          path: '/aqy/realtime'
        },
        {
          name: '大屏展示',
          icon: 'el-icon-full-screen',
          path: '/screen',
          isExternal: true
        }
      ],
      tableHeaderStyle: {
        background: '#f5f7fa',
        color: '#606266',
        fontWeight: 500,
        fontSize: '14px'
      },
      tableCellStyle: {
        fontSize: '14px'
      },

      // websocket部分
      ws: null,
      wsUrl: 'ws://localhost:7070/prod-api/websocket/message',
      isConnect: false,
      rec: null,
      checkMsg: {hmsg: 'heartbeat'},
      timeout: 20000,
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
    if (this.ws) {
      this.ws.close();
    }
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

    // WebSocket相关方法
    initWebSocket() {
      let that = this;
      that.ws = new WebSocket(that.wsUrl);
      that.ws.onopen = that.webSocketOnOpen;
      that.ws.onmessage = that.webSocketOnMessage;
      that.ws.onclose = that.webSocketClose;
      that.ws.onerror = that.webSocketOnError;
    },
    webSocketOnOpen() {
      let that = this;
      that.isConnect = true;
      that.timeoutObj = setTimeout(function () {
        if (that.isConnect)
          that.ws.send(JSON.stringify(that.checkMsg));
      }, that.timeout);
    },
    webSocketOnError() {
      this.isConnect = false;
      this.reConnect();
    },
    webSocketOnMessage(e) {
      if (e.data && e.data.indexOf('{') === 0) {
        var jsonData = JSON.parse(e.data);
        if (jsonData) {
          switch (jsonData.type) {
            case 1:
              this.getEqmtGroupTypes(this.projectId);
              this.getEqmtList(this.projectId);
              break;
            case 2:
            case 3:
            case 4:
            case 5:
              this.getEqmtList(this.projectId);
              break;
          }
        }
      }
    },
    webSocketClose() {
      this.isConnect = false;
      this.reConnect();
    },
    reConnect() {
      let that = this;
      if (that.isConnect) return;
      clearTimeout(that.rec);
      that.rec = setTimeout(function () {
        that.initWebSocket();
      }, 5000);
    },

    // UI相关方法
    getProjectTypeTag(type) {
      const typeMap = {
        '1': 'success',
        '2': 'warning',
        '3': 'danger'
      };
      return typeMap[type] || 'info';
    },
    getCardIcon(type) {
      const iconMap = {
        '位移监测': 'el-icon-position',
        '裂缝监测': 'el-icon-warning',
        '水位监测': 'el-icon-water-cup',
        '倾角监测': 'el-icon-refresh',
        '监测断面': 'el-icon-view',
        '设备管理': 'el-icon-setting',
        '监控设备': 'el-icon-monitor',
        '设备资料': 'el-icon-document',
        '报警等级': 'el-icon-warning',
        '实时数据': 'el-icon-data-line',
        '大屏展示': 'el-icon-full-screen'
      };
      return iconMap[type] || 'el-icon-monitor';
    },
    getDeviceIcon(type) {
      return this.getCardIcon(type);
    },
    getAlarmLevelType(level) {
      const levelMap = {
        '0': 'success',
        '1': 'info',
        '2': 'warning',
        '3': 'danger'
      };
      return levelMap[level] || 'info';
    },
    handleMenuClick(menu) {
      if (menu.isExternal) {
        window.open(menu.path, '_blank');
      } else {
        this.$router.push(menu.path);
      }
    },
    refreshData() {
      this.getEqmtGroupTypes(this.projectId);
      this.getEqmtList(this.projectId);
    },
    formatOnlineStatus(status) {
      return status === 0 ? '在线' : '离线';
    }
  },
  computed: {
    onlineRate() {
      if (!this.eqmtByTypes.length) return 0;
      const total = this.eqmtByTypes.reduce((sum, item) => sum + item.totalCount, 0);
      const online = this.eqmtByTypes.reduce((sum, item) => sum + item.onlineCount, 0);
      return total ? Math.round((online / total) * 100) : 0;
    }
  }
};
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;
}

// 顶部数据卡片样式
.overview-cards {
  margin-bottom: 20px;

  .data-card {
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 20px rgba(0,0,0,0.1);
    }

    .card-content {
      display: flex;
      align-items: center;

      .card-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        background: #ecf5ff;

        i {
          font-size: 24px;
          color: #409EFF;
        }
      }

      .card-info {
        flex: 1;

        h4 {
          margin: 0;
          font-size: 14px;
          color: #666;
        }

        .card-numbers {
          margin: 8px 0;
          font-size: 24px;
          font-weight: bold;

          .online {
            color: #409EFF;
          }

          .divider {
            margin: 0 4px;
            color: #999;
          }

          .total {
            color: #666;
            font-size: 18px;
          }
        }

        .status-bar {
          height: 4px;
          background: #f0f0f0;
          border-radius: 2px;

          .progress {
            height: 100%;
            background: #409EFF;
            border-radius: 2px;
            transition: width 0.3s ease;
          }
        }
      }
    }
  }
}

// 中间区域样式
.middle-section {
  margin-bottom: 20px;

  .project-info-card {
    background: white;
    border-radius: 8px;
    padding: 20px;
    height: 100%;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      h3 {
        margin: 0;
        font-size: 18px;
      }
    }

    .project-desc {
      color: #666;
      line-height: 1.6;
      margin-bottom: 20px;
    }

    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;

      .info-item {
        .label {
          color: #999;
          margin-right: 8px;
        }

        .value {
          color: #333;
          font-weight: 500;
        }
      }
    }
  }

  .quick-menu {
    background: white;
    border-radius: 8px;
    padding: 20px;
    height: 100%;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);

    .menu-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 16px;
      margin-top: 20px;

      .menu-item {
        text-align: center;
        padding: 16px;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;
        background: #f8f9fa;

        &:hover {
          background: #ecf5ff;
          color: #409EFF;
        }

        i {
          font-size: 24px;
          margin-bottom: 8px;
          display: block;
        }

        span {
          font-size: 14px;
        }
      }
    }
  }
}

// 底部区域样式
.bottom-section {
  .device-table-card {
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      h3 {
        margin: 0;
        font-size: 18px;
      }
    }

    .device-type {
      display: flex;
      align-items: center;
      justify-content: center;

      i {
        margin-right: 8px;
        font-size: 16px;
        color: #409EFF;
      }
    }
  }

  .monitor-card {
    background: white;
    border-radius: 8px;
    padding: 20px;
    height: 100%;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      h3 {
        margin: 0;
        font-size: 18px;
      }

      .el-dropdown-link {
        cursor: pointer;
        color: #409EFF;
      }
    }

    .monitor-content {
      .camera-view {
        position: relative;
        border-radius: 8px;
        overflow: hidden;
        background: #000;
        height: 300px;
        display: flex;
        align-items: center;
        justify-content: center;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .camera-overlay {
          position: absolute;
          top: 12px;
          left: 12px;

          .status-badge {
            background: rgba(0,0,0,0.6);
            color: white;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;

            i {
              color: #67C23A;
              margin-right: 4px;
            }
          }
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .overview-cards {
    .data-card {
      margin-bottom: 16px;
    }
  }

  .middle-section {
    .quick-menu {
      .menu-grid {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  }

  .monitor-card {
    .camera-view {
      height: 200px;
    }
  }
}
</style>
