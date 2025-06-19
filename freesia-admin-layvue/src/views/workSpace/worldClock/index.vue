<template>
  <div class="world-clock">
    <h2>世界时钟</h2>
    <div class="clocks-container">
      <div v-for="clock in clocks" :key="clock.timezone" class="clock-card">
        <div class="location">
          <lay-avatar :src="clock.flag"></lay-avatar>
          {{ clock.location }}
        </div>
        <div class="time">{{ clock.time }}:{{ clock.seconds }}</div>
        <!--        <div class="seconds">{{ clock.seconds }}</div>-->
        <div class="timezone">{{ clock.timezone }}</div>
        <div class="date">{{ clock.date }}</div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, onMounted, onBeforeUnmount, reactive} from 'vue';

interface Clock {
  location: string;
  timezone: string;
  time: string;
  seconds: string;
  date: string;
  timer?: number;
  flag?: string;
}

export default defineComponent({
  name: 'WorldClock',
  setup() {
    const clocks = reactive<Clock[]>([
      {location: '上海', timezone: 'Asia/Shanghai', flag: '/src/assets/flag/中国国旗.svg'},
      {location: '东京', timezone: 'Asia/Tokyo', flag: '/src/assets/flag/日本国旗.svg'},
      {location: '伦敦', timezone: 'Europe/London', flag: '/src/assets/flag/英国国旗.svg'},
      {location: '柏林', timezone: 'Europe/Berlin', flag: '/src/assets/flag/德国.svg'},
      {location: '纽约（美东）', timezone: 'America/New_York', flag: '/src/assets/flag/美国国旗.svg'},
      {location: '丹佛（美中）', timezone: 'America/Denver', flag: '/src/assets/flag/美国国旗.svg'},
      {location: '洛杉矶（美西）', timezone: 'America/Los_Angeles', flag: '/src/assets/flag/美国国旗.svg'},
      {location: '悉尼', timezone: 'Australia/Sydney', flag: '/src/assets/flag/澳大利亚国旗.svg'},
      {location: '布宜诺斯艾利斯', timezone: 'America/Argentina/Buenos_Aires', flag: '/src/assets/flag/阿根廷.svg'},
      {location: '圣地亚哥', timezone: 'America/Santiago', flag: '/src/assets/flag/智利.svg'},
      {location: '利雅得', timezone: 'Asia/Riyadh', flag: '/src/assets/flag/沙特阿拉伯.svg'},
      {location: '约翰内斯堡', timezone: 'Africa/Johannesburg', flag: '/src/assets/flag/South Africa.svg'},
    ].map(clock => ({
      ...clock,
      time: '',
      seconds: '',
      date: ''
    })));

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

    onMounted(() => {
      clocks.forEach(clock => {
        // 初始更新
        updateClock(clock);
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

    return {
      clocks
    };
  }
});
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

.date {
  font-size: 0.9rem;
  color: #7f8c8d;
}
</style>