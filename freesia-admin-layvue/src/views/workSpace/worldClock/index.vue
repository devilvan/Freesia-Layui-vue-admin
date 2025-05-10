<template>
  <div class="world-clock">
    <h2>世界时钟</h2>
    <div class="clocks-container">
      <div v-for="clock in clocks" :key="clock.timezone" class="clock-card">
        <div class="location">{{ clock.location }}</div>
        <div class="time">{{ clock.time }}</div>
        <div class="seconds">{{ clock.seconds }}</div>
        <div class="timezone">{{ clock.timezone }}</div>
        <div class="date">{{ clock.date }}</div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted, onBeforeUnmount, reactive } from 'vue';

interface Clock {
  location: string;
  timezone: string;
  time: string;
  seconds: string;
  date: string;
  timer?: number;
}

export default defineComponent({
  name: 'WorldClock',
  setup() {
    const clocks = reactive<Clock[]>([
      { location: '上海', timezone: 'Asia/Shanghai' },
      { location: '东京', timezone: 'Asia/Tokyo' },
      { location: '伦敦', timezone: 'Europe/London' },
      { location: '纽约', timezone: 'America/New_York' },
      { location: '悉尼', timezone: 'Australia/Sydney' },
      { location: '巴黎', timezone: 'Europe/Paris' }
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