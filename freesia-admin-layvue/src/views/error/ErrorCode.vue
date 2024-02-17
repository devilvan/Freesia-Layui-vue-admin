<template>
  <div>
    <component :is="errorCodeComponent"></component>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import Error401 from './401.vue';
import Error403 from './403.vue';
import Error404 from './404.vue';
import Error500 from './500.vue';

export default defineComponent({
  components: {
    Error401,
    Error403,
    Error404,
    Error500,
  },
  computed: {
    errorCodeComponent() {
      const code = this.$route.params.code;
      switch (code) {
        case '401':
          return Error401;
        case '403':
          return Error403;
        case '404':
          return Error404;
        default:
          return Error500;
      }
    },
  },
});
</script>
