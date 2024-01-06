<template>
  <div :style="'height:' + height">
    <iframe
        :id="props.iframeId"
        :iframeId="props.iframeId"
        style="width: 100%; height: 100%"
        :src="props.src + '?' + new Date().getTime()"
        :frameborder="'yes'"
    ></iframe>
  </div>
</template>

<script lang="ts" setup>
import {defineComponent, ref} from "vue";

defineComponent({
  name: "InnerLink"
})

interface InnerLinkProps {
  src: string,
  iframeId?: string
}

const props = withDefaults(defineProps<InnerLinkProps>(), {
  src: "/"
});
const height = ref(820 + "px");
</script>
