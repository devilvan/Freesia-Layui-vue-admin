<template>
  <div class="world-clock">
    <h2>世界时钟</h2>
    <div class="clocks-container">
      <div v-for="clock in clocks" :key="clock.timezone" class="clock-card">
        <div class="location">
          <lay-avatar size="lg" :src="clock.flag"></lay-avatar>
          {{ clock.title }}
        </div>
        <div class="time">{{ clock.time }}:{{ clock.seconds < 10 ? '0' + clock.seconds : clock.seconds }}</div>
        <div class="sunRiseSet">
          <div class="sunrise">
            <object style="height: 40px" :data="'/svg/sunrise.svg'" type="image/svg+xml"></object>
            <div>{{ clock.sunriseTimeLocal }}</div>
          </div>
          <div class="sunrise_sunset_middle">
            <div>日出日落时间</div>
            <div>日长：{{ clock.dayLengthMinutes }}分钟</div>
          </div>
          <div class="sunset">
            <object style="height: 40px" :data="'/svg/sunset.svg'" type="image/svg+xml"></object>
            <div>{{ clock.sunsetTimeLocal }}</div>
          </div>
        </div>
        <div class="timezone">{{ clock.timezone }}</div>
        <div class="date">{{ clock.date }}</div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: "WorldClock",
};
</script>

<script lang="ts" setup>
import {onMounted, onBeforeUnmount, reactive, ref} from 'vue';
import {Clock, FindCitySunriseSunsetEntity, FindCitySunriseSunsetReqDto} from "@/types/workSpace/WorldClock";
import * as echarts from "echarts";
import {findCitySunriseSunset} from "@/api/worldclock/WorldClock";
import {formatDateTime} from "@/util/UDate";
import {R} from "@/types/Result";


/*INIT*/
onMounted(() => {
  clocks.value.forEach(clock => {
    // 初始更新
    updateClock(clock);
    // doBuildSunProgress();
    // 为每个时钟设置独立的定时器
    clock.timer = window.setInterval(() => updateClock(clock), 1000);
  });
  // 条件查询城市日出日落时间表
  doFindCitySunriseSunset();
});

onBeforeUnmount(() => {
  clocks.value.forEach(clock => {
    if (clock.timer) {
      clearInterval(clock.timer);
    }
  });
});
/*INIT*/

/*VAR*/
const clocks = ref<Clock[]>([
  {title: '北京', location: '北京', timezone: 'Asia/Shanghai', flag: '/flag/China.svg'},
  {title: '东京', location: '东京', timezone: 'Asia/Tokyo', flag: '/flag/Japan.svg'},
  {title: '伦敦', location: '伦敦', timezone: 'Europe/London', flag: '/flag/Britain.svg'},
  {title: '柏林', location: '柏林', timezone: 'Europe/Berlin', flag: '/flag/Germany.svg'},
  {title: '纽约（美东）', location: '纽约', timezone: 'America/New_York', flag: '/flag/United States.svg'},
  {title: '丹佛（美中）', location: '丹佛', timezone: 'America/Denver', flag: '/flag/United States.svg'},
  {title: '洛杉矶（美西）', location: '洛杉矶', timezone: 'America/Los_Angeles', flag: '/flag/United States.svg'},
  {title: '悉尼', location: '悉尼', timezone: 'Australia/Sydney', flag: '/flag/Australia.svg'},
  {
    title: '布宜诺斯艾利斯',
    location: '布宜诺斯艾利斯',
    timezone: 'America/Argentina/Buenos_Aires',
    flag: '/flag/Argentina.svg'
  },
  // {title: '圣地亚哥', location: '圣地亚哥', timezone: 'America/Santiago', flag: '/flag/Chile.svg'},
  {title: '马德里', location: '马德里', timezone: 'Europe/Madrid', flag: '/flag/Spanish.svg'},

  {title: '利雅得', location: '利雅得', timezone: 'Asia/Riyadh', flag: '/flag/Saudi.svg'},
  {title: '约翰内斯堡', location: '约翰内斯堡', timezone: 'Africa/Johannesburg', flag: '/flag/South Africa.svg'},
].map(clock => ({
  ...clock,
  time: '',
  seconds: '',
  date: ''
})));
/*VAR*/

/*FUNCTION*/
const updateClock = (clock: Clock) => {
  const now = new Date();

  // 格式化时间部分
  const timeOptions: Intl.DateTimeFormatOptions = {
    hour: '2-digit',
    minute: '2-digit',
    timeZone: clock.timezone
  };
  clock.time = new Intl.DateTimeFormat('zh-CN', timeOptions).format(now);

  // 单独获取秒数
  const secondsOptions: Intl.DateTimeFormatOptions = {
    second: '2-digit',
    timeZone: clock.timezone
  };
  clock.seconds = new Intl.DateTimeFormat('zh-CN', secondsOptions).format(now);

  // 格式化日期部分
  const dateOptions: Intl.DateTimeFormatOptions = {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    timeZone: clock.timezone
  };
  clock.date = new Intl.DateTimeFormat('zh-CN', dateOptions).format(now);
};

function doFindCitySunriseSunset() {
  let today = formatDateTime(new Date(), 'yyyy-MM-dd');
  let cityNameList = clocks.value.map(item => item.location);
  let params: FindCitySunriseSunsetReqDto = {
    date: today,
    cityNameList: cityNameList
  }
  findCitySunriseSunset(params).then((res: R<FindCitySunriseSunsetEntity[]>) => {
    if (res.code === 200) {
      let data = res.data;
      data?.forEach((item: FindCitySunriseSunsetEntity, index: number) => {
        let clock = clocks.value.find(i => i.location === item.cityName);
        if (clock) {
          clock.sunRiseTime = item.sunriseTime
          clock.sunSetTime = item.sunsetTime
          clock.sunriseTimeLocal = item.sunriseTimeLocal
          clock.sunsetTimeLocal = item.sunsetTimeLocal
          clock.dayLengthMinutes = item.dayLengthMinutes
        }
      })
    }
  })
}

/*FUNCTION*/
</script>

<style scoped>
.world-clock {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.clocks-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.clock-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.3s ease;
}

.clock-card:hover {
  transform: translateY(-5px);
}

.location {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 10px;
  color: #2c3e50;
}

.time {
  font-size: 2.5rem;
  font-weight: bold;
  margin: 10px 0;
  color: #27ae60;
  display: flex;
  justify-content: center;
  align-items: baseline;
}

.seconds {
  font-size: 1.2rem;
  display: inline-block;
  min-width: 30px;
  color: #e74c3c;
}

.timezone {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin-bottom: 5px;
}

.sunRiseSet {
  display: flex;
  font-size: 0.9rem;
  justify-content: space-between;
  margin-bottom: 5px;
  padding-top: 5px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.sunrise, .sunset {
  text-align: center;
}

.sunrise_sunset_middle {
  text-align: center;
  justify-content: center;
  align-items: center;
  margin-top: 10px;
}

.date {
  font-size: 0.9rem;
  color: #7f8c8d;
}
</style>