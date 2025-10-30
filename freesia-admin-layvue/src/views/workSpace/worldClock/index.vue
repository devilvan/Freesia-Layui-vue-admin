<template>
  <div class="world-clock">
    <h2>世界时钟</h2>
    <div class="clocks-container">
      <div v-for="clock in clocks" :key="clock.timezone" class="clock-card">
        <div class="location">
          <lay-avatar size="lg" :src="clock.flag"></lay-avatar>
          {{ clock.location }}
        </div>
        <div class="time">{{ clock.time }}:{{ clock.seconds < 10 ? '0' + clock.seconds : clock.seconds }}</div>
        <div class="sunRiseSet">
          <div class="sunrise">
            <object style="height: 40px" :data="'/svg/sunrise.svg'" type="image/svg+xml"></object>
            <div>09:30</div>
          <div class="sunset">
            <object style="height: 40px" :data="'/svg/sunset.svg'" type="image/svg+xml"></object>
            <div>18:30</div>
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
import {Clock} from "@/types/workSpace/WorldClock";
import * as echarts from "echarts";


/*INIT*/
onMounted(() => {
  clocks.forEach(clock => {
    // 初始更新
    updateClock(clock);
    // doBuildSunProgress();
    // 为每个时钟设置独立的定时器
    clock.timer = window.setInterval(() => updateClock(clock), 1000);
  });
});

onBeforeUnmount(() => {
  clocks.forEach(clock => {
    if (clock.timer) {
      clearInterval(clock.timer);
    }
  });
});
/*INIT*/

/*VAR*/
const clocks = reactive<Clock[]>([
  {location: '上海', timezone: 'Asia/Shanghai', flag: '/flag/China.svg'},
  {location: '东京', timezone: 'Asia/Tokyo', flag: '/flag/Japan.svg'},
  {location: '伦敦', timezone: 'Europe/London', flag: '/flag/Britain.svg'},
  {location: '柏林', timezone: 'Europe/Berlin', flag: '/flag/Germany.svg'},
  {location: '纽约（美东）', timezone: 'America/New_York', flag: '/flag/United States.svg'},
  {location: '丹佛（美中）', timezone: 'America/Denver', flag: '/flag/United States.svg'},
  {location: '洛杉矶（美西）', timezone: 'America/Los_Angeles', flag: '/flag/United States.svg'},
  {location: '悉尼', timezone: 'Australia/Sydney', flag: '/flag/Australia.svg'},
  {location: '布宜诺斯艾利斯', timezone: 'America/Argentina/Buenos_Aires', flag: '/flag/Argentina.svg'},
  {location: '圣地亚哥', timezone: 'America/Santiago', flag: '/flag/Chile.svg'},
  {location: '利雅得', timezone: 'Asia/Riyadh', flag: '/flag/Saudi.svg'},
  {location: '约翰内斯堡', timezone: 'Africa/Johannesburg', flag: '/flag/South Africa.svg'},
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

.date {
  font-size: 0.9rem;
  color: #7f8c8d;
}
</style>