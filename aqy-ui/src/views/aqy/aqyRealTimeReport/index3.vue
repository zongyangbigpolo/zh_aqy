<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工程项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择工程项目">
          <el-option v-for="(item, index) in projectOptions" :key='index' :value="item.id" :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备类型" prop="eqmtTypeId">
        <el-select v-model="queryParams.eqmtTypeId" placeholder="请选择设备类型" @change="handleEmqtTypeChange">
          <el-option v-for="(item, index) in eqmtTypeOptions" :key='index' :value="item.id" :label="item.eqmtTypeName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备名称" prop="eqmtId">
        <el-select
          v-model="queryParams.eqmtId"
          placeholder="请选择设备名称"
          clearable
          :disabled="!queryParams.eqmtTypeId"
        >
          <el-option
            v-for="(item, index) in eqmtOptions"
            :key='index'
            :value="item.id"
            :label="item.eqmtName"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="采集时间" prop="timeFrame">
        <el-date-picker
          v-model="queryParams.timeFrame"
          type="datetimerange"
          value-format="yyyy-MM-dd HH:mm:ss"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期">
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
          v-hasPermi="['aqy:aqyEquipment:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
      <el-tab-pane label="数据表格" name="first">
        <el-table v-loading="loading" :data="dataList" :max-height="tableHeight" border style="width: 100%;">
          <el-table-column label="序号" type="index" align="center"/>
          <el-table-column label="设备类型" align="center" prop="eqmtTypeName"/>
          <el-table-column label="设备名称" align="center" prop="eqmtName"/>
          <el-table-column label="设备编码" align="center" prop="eqmtCode"/>
          <el-table-column label="X位移(mm)" align="center" prop="xValue" v-if="searchType === 'WY'"/>
          <el-table-column label="Y位移(mm)" align="center" prop="yValue" v-if="searchType === 'WY'"/>
          <el-table-column label="裂缝(mm)" align="center" prop="lfValue" v-if="searchType === 'LF'"/>
          <el-table-column label="X角度(°)" align="center" prop="xvalueQj" v-if="searchType === 'QJ'"/>
          <el-table-column label="Y角度(°)" align="center" prop="yvalueQj" v-if="searchType === 'QJ'"/>
          <el-table-column label="Z角度(°)" align="center" prop="zvalueQj" v-if="searchType === 'QJ'"/>
          <el-table-column label="水位(mm)" align="center" prop="ylValue" v-if="searchType === 'YL'"/>
          <el-table-column label="采集时间" align="center" prop="catchTime">
            <template slot-scope="scope">
              <span v-if="scope.row.catchTime != null">
                {{ parseTime(scope.row.catchTime, "{y}-{m}-{d} {h}:{i}:{s}") }}
              </span>
            </template>
          </el-table-column>
        </el-table>
        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-tab-pane>
      <el-tab-pane label="统计图表" name="second">
        <div ref="chart" class="chart-container"></div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import { listProject } from "@/api/aqy/project";
import { listAqyEquipmentType } from "@/api/aqy/aqyEquipmentType";
import { listAqyEquipment } from "@/api/aqy/aqyEquipment";
import { listRealTime } from "@/api/aqy/aqyRawReport";
import { parseTime } from "../../../utils/ruoyi";
import * as XLSX from 'xlsx';
import * as echarts from 'echarts';
import moment from 'moment';
import {
  TitleComponent,
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  DataZoomComponent
} from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { UniversalTransition } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';

echarts.use([
  TitleComponent,
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  DataZoomComponent,
  LineChart,
  CanvasRenderer,
  UniversalTransition
]);

export default {
  name: "AqyEquipment",
  data() {
    return {
      timeFrame: [],
      myChart: null,
      loading: true,
      total: 0,
      tableHeight: document.documentElement.scrollHeight - 330,
      showSearch: true,
      activeName: 'first',
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        projectId: null,
        eqmtTypeId: null,
        eqmtId: null,
        eqmtCode: null,
        timeFrame: [moment().subtract(1, "months").format('YYYY-MM-DD HH:mm:ss'), moment().format('YYYY-MM-DD HH:mm:ss')]
      },
      projectOptions: [],
      eqmtTypeOptions: [],
      eqmtOptions: [],
      dataList: [],
      searchType: null,
      chartType: 'line',
    };
  },
  created() {
    this.getProjects();
  },
  methods: {
    parseTime,
    getProjects() {
      listProject().then(response => {
        this.projectOptions = response.rows;
        if (response.rows)
          this.queryParams.projectId = response.rows[0].id;
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

        this.getList();
      });
    },
    getList() {
      this.loading = true;
      let eqmtTypeItem = this.eqmtTypeOptions.filter(item => {
        return item.id === this.queryParams.eqmtTypeId;
      });
      if (eqmtTypeItem && eqmtTypeItem.length > 0) {
        this.searchType = eqmtTypeItem[0].eqmtTypeSymbol;
      }
      listRealTime(this.queryParams).then(response => {
        this.dataList = response.rows;
        this.total = response.total;
        this.loading = false;
        this.updateChartData();
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

        // 获取设备列表
        listAqyEquipment({ eqmtTypeId: val }).then(response => {
          this.eqmtOptions = response.rows;
        });

        // 清空图表实例
        if (this.myChart) {
          this.myChart.dispose();
          this.myChart = null;
        }

        // 获取数据列表
        this.getList();
      }
    },
    updateChartData() {
      if (this.activeName === 'second') {
        this.renderChart();
      }
    },
    handleClick(tab, event) {
      if (tab.name === 'second') {
        this.$nextTick(() => {
          if (this.myChart) {
            this.myChart.resize();
          }
          this.renderChart();
        });
      }
    },
    renderChart() {
      const chartDom = this.$refs.chart;
      if (this.myChart) {
        this.myChart.dispose();
      }
      this.myChart = echarts.init(chartDom);

      // 定义主题色
      const themes = {
        // 科技蓝主题
        tech: {
          backgroundColor: '#1a1c1d',
          gradientColors: {
            WY: ['#00c6fb', '#005bea'],
            LF: ['#f83600', '#f9d423'],
            YL: ['#4facfe', '#00f2fe'],
            QJ: ['#0ba360', '#3cba92']
          },
          textColor: '#e1e1e1',
          lineColor: 'rgba(255,255,255,0.2)',
          splitLineColor: 'rgba(255,255,255,0.1)'
        },
        // 暗金主题
        dark: {
          backgroundColor: '#0f1c2c',
          gradientColors: {
            WY: ['#FFD700', '#DAA520'],
            LF: ['#FFA500', '#FF4500'],
            YL: ['#B8860B', '#CD853F'],
            QJ: ['#FFD700', '#FF8C00']
          },
          textColor: '#FFD700',
          lineColor: 'rgba(218,165,32,0.2)',
          splitLineColor: 'rgba(218,165,32,0.1)'
        },
        // 霓虹主题
        neon: {
          backgroundColor: '#16161a',
          gradientColors: {
            WY: ['#ff0080', '#7928ca'],
            LF: ['#ff4d4d', '#f9cb28'],
            YL: ['#00ff87', '#60efff'],
            QJ: ['#ff0080', '#ff8c00']
          },
          textColor: '#fff',
          lineColor: 'rgba(255,255,255,0.2)',
          splitLineColor: 'rgba(255,255,255,0.1)'
        }
      };

      // 选择主题 (可以根据需要切换 'tech', 'dark', 'neon')
      const currentTheme = themes.neon;

      let option = {
        backgroundColor: currentTheme.backgroundColor,
        title: {
          text: '监测数据统计图表',
          left: 'center',
          top: 20,
          textStyle: {
            color: currentTheme.textColor,
            fontSize: 24,
            fontWeight: 'bold',
            textShadow: '2px 2px 4px rgba(0,0,0,0.3)'
          }
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(0,0,0,0.8)',
          borderColor: currentTheme.lineColor,
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          },
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#000'
            },
            crossStyle: {
              color: currentTheme.textColor
            },
            lineStyle: {
              type: 'dashed',
              width: 1
            }
          }
        },
        toolbox: {
          feature: {
            saveAsImage: {
              backgroundColor: currentTheme.backgroundColor
            },
            myTool: {
              show: true,
              title: '切换图表',
              icon: 'path://M512 64c-247.4 0-448 200.6-448 448s200.6 448 448 448 448-200.6 448-448-448-448-448-448z',
              onclick: () => {
                this.toggleChartType();
              }
            }
          },
          right: '5%',
          top: '5%',
          iconStyle: {
            borderColor: currentTheme.textColor,
            borderWidth: 1,
            color: currentTheme.textColor
          }
        },
        legend: {
          data: this.getSeriesData().map(series => series.name),
          textStyle: {
            color: currentTheme.textColor,
            fontSize: 12
          },
          top: '10%',
          icon: 'roundRect',
          itemWidth: 12,
          itemHeight: 12,
          itemGap: 25
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '25%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: this.chartType === 'bar',
          data: this.getXAxisData(),
          axisLabel: {
            color: currentTheme.textColor,
            fontSize: 12,
            rotate: 45,
            formatter: function(value) {
              return value.split(' ')[1]; // 只显示时间部分
            }
          },
          axisLine: {
            lineStyle: {
              color: currentTheme.lineColor,
              width: 2
            }
          },
          splitLine: {
            show: false
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            color: currentTheme.textColor,
            fontSize: 12,
            formatter: '{value}'
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: currentTheme.lineColor,
              width: 2
            }
          },
          splitLine: {
            lineStyle: {
              color: currentTheme.splitLineColor,
              type: 'dashed',
              width: 1
            }
          }
        },
        dataZoom: [
          {
            type: 'slider',
            show: true,
            xAxisIndex: [0],
            start: 0,
            end: 100,
            height: 20,
            bottom: '2%',
            borderColor: 'transparent',
            backgroundColor: currentTheme.splitLineColor,
            fillerColor: currentTheme.lineColor,
            handleStyle: {
              color: currentTheme.textColor,
              borderColor: currentTheme.textColor
            },
            textStyle: {
              color: currentTheme.textColor
            },
            handleIcon: 'path://M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
            emphasis: {
              handleStyle: {
                borderColor: currentTheme.textColor,
                color: currentTheme.textColor
              }
            }
          },
          {
            type: 'inside',
            xAxisIndex: [0],
            start: 0,
            end: 100
          }
        ]
      };

      // 获取数据系列并应用样式
      const series = this.getSeriesData().map((item, index) => {
        const gradientColor = currentTheme.gradientColors[this.searchType];
        return {
          ...item,
          smooth: true,
          symbolSize: 8,
          symbol: 'circle',
          lineStyle: {
            width: 4,
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0,
                color: gradientColor[0]
              }, {
                offset: 1,
                color: gradientColor[1]
              }]
            },
            shadowColor: gradientColor[0],
            shadowBlur: 10
          },
          areaStyle: this.chartType === 'line' ? {
            opacity: 0.2,
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0,
                color: gradientColor[0]
              }, {
                offset: 1,
                color: 'transparent'
              }]
            }
          } : null,
          itemStyle: {
            color: gradientColor[0],
            borderWidth: 2,
            borderColor: '#fff',
            shadowColor: gradientColor[0],
            shadowBlur: 10
          },
          emphasis: {
            scale: true,
            focus: 'series',
            itemStyle: {
              borderWidth: 3,
              borderColor: '#fff',
              shadowBlur: 20,
              shadowColor: gradientColor[0]
            }
          }
        };
      });

      option.series = series;
      this.myChart.setOption(option);
    },
    getXAxisData() {
      return this.dataList.map(item => {
        const date = new Date(item.catchTime);
        return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`;
      });
    },
    getSeriesData() {
      let series = [];
      switch(this.searchType) {
        case 'WY':
          series = [
            {
              name: 'X位移',
              type: this.chartType,
              data: this.dataList.map(item => item.xValue)
            },
            {
              name: 'Y位移',
              type: this.chartType,
              data: this.dataList.map(item => item.yValue)
            }
          ];
          break;
        case 'LF':
          series = [
            {
              name: '裂缝',
              type: this.chartType,
              data: this.dataList.map(item => item.lfValue)
            }
          ];
          break;
        case 'YL':
          series = [
            {
              name: '水位',
              type: this.chartType,
              data: this.dataList.map(item => item.ylValue)
            }
          ];
          break;
        case 'QJ':
          series = [
            {
              name: 'X角度',
              type: this.chartType,
              data: this.dataList.map(item => item.xvalueQj)
            },
            {
              name: 'Y角度',
              type: this.chartType,
              data: this.dataList.map(item => item.yvalueQj)
            },
            {
              name: 'Z角度',
              type: this.chartType,
              data: this.dataList.map(item => item.zvalueQj)
            }
          ];
          break;
      }
      return series;
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams.pageNum = 1;
      this.queryParams.eqmtId = null;
      this.eqmtOptions = [];
      this.getProjects();
    },
    handleExport() {
      const exportData = this.prepareExportData();
      const worksheet = XLSX.utils.json_to_sheet(exportData);
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, '设备数据');
      XLSX.writeFile(workbook, `设备数据_${new Date().getTime()}.xlsx`);
    },
    prepareExportData() {
      const data = this.dataList.map(item => {
        return {
          '设备类型': item.eqmtTypeName,
          '设备名称': item.eqmtName,
          '设备编码': item.eqmtCode,
          '采集时间': this.parseTime(item.catchTime, "{y}-{m}-{d} {h}:{i}:{s}"),
          ...(this.searchType === 'WY' && {
            'X位移(mm)': item.xValue,
            'Y位移(mm)': item.yValue,
          }),
          ...(this.searchType === 'LF' && {
            '裂缝(mm)': item.lfValue,
          }),
          ...(this.searchType === 'QJ' && {
            'X角度(°)': item.xvalueQj,
            'Y角度(°)': item.yvalueQj,
            'Z角度(°)': item.zvalueQj,
          }),
          ...(this.searchType === 'YL' && {
            '水位(mm)': item.ylValue,
          }),
        };
      });
      return data;
    },
    toggleChartType() {
      if (this.chartType === 'line') {
        this.chartType = 'bar';
      } else {
        this.chartType = 'line';
      }
      this.renderChart();
    },
  }
}
</script>
<style scoped>
.chart-container {
  width: 100%;
  height: 600px;
  background: #16161a; /* 根据选择的主题更改背景色 */
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.3);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.1);
}

/* 添加响应式设计 */
@media screen and (max-width: 768px) {
  .chart-container {
    height: 400px;
    padding: 10px;
  }
}
</style>
