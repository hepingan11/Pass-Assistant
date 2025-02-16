<template>
  <div style="position: absolute; width: 120px">
    <el-button @click="setCheckFalse" v-if="check" color="var(--themeColor2)" style="width: 80%;margin-bottom: 5px;margin-left: 15px;margin-top: 3px">收起</el-button>
    <el-button @click="setCheckTrue" v-if="check === false" color="var(--themeColor2)" style="position: absolute;width: 80%;margin-bottom: 5px;margin-left: 15px;margin-top: 3px;width: 80px">展开</el-button>
    <div v-if="check">
      <div>
        <el-image src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page/839fea7fbfca164933a7b965c5809a47.jpg" class="check-img"></el-image>
      </div>
      <div @click="pushTo('/zhipuvideo')">
        <el-image src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page/cbd73b7fb0d806bedc10b0871241854c.jpg" class="check-img"></el-image>
      </div>
      <div @click="pushTo('/zhipu')">
        <el-image src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page/ba5a85d36e851cf5b86290bf27c15ab4.jpg" class="check-img"></el-image>
      </div>
      <div>
        <el-image src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page/1bd6a0d124b69e51056509db7c768994.jpg" class="check-img"></el-image>
      </div>
    </div>
  </div>
</template>

<script>
  import router from "@/router";
  import {ref} from "vue";

  export default {
    name: "ZhipuSelect",
    methods: {
      router() {
        return router;
      },
    },
    setup(){
      function pushTo(data){
        router.push({
          path: data,
        });
      }

      const check = ref(true)

      function setCheckFalse(){
        check.value = false
      }

      function setCheckTrue(){
        check.value = true
      }

      return{
        pushTo,
        setCheckFalse,
        setCheckTrue,
        check
      }
    }
  }
</script>
<style scoped>
.check-img{
  width: 100px;
  height: 100px;
  border-radius: 10px;
  margin-bottom: 10px;
  margin-left: 10px;
}

.check-img:hover{
  border: 3px solid var(--themeColor2);
}
</style>