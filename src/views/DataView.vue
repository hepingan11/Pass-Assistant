<template>
  <div class="body">
<!--    <div class="content">-->

      <div class="card" @click="router().push({ path: '/data_flourish_view' })">
        <div class="head">
          <img src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/link/c8df4095-f282-4bb2-9974-e31148a3868a.jpg" class="index"/>
          <img src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page1/045213s7pbRmLAE8IAPVgr.png" class="cover"/>
          <img src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page1/151353qb72wfLgD060lkqd.png" class="title"/>
        </div>
      </div>

      <div class="card" @click="router().push({ path: '/data_test_view' })">
        <div class="head">
          <img src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page1/032035ng9DENqpOsOtQRef.png" class="title"/>
          <img src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page1/033638twZgVfdfd93kKIQM.png" class="cover"/>
          <img src="https://img.zcool.cn/community/01f1325ca6b206a80121416850a75c.jpg@3000w_1l_0o_100sh.jpg" class="index"/>
        </div>
      </div>

    <div class="card" @click="router().push({ path: '/data_test_view' })">
      <div class="head">
        <img src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page1/032035ng9DENqpOsOtQRef.png" class="title"/>
        <img src="https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/page1/033638twZgVfdfd93kKIQM.png" class="cover"/>
        <img src="https://img.zcool.cn/community/01f1325ca6b206a80121416850a75c.jpg@3000w_1l_0o_100sh.jpg" class="index"/>
      </div>
    </div>


<!--    </div>-->
  </div>
</template>

<script>
  import router from "@/router";

  export default {
    name: "DataView",
    methods: {
      router() {
        return router
      },
    },
  }
</script>

<style lang="scss" scoped>
.body {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  flex-direction: column;
  align-items: center;
  display: flex;
  flex-wrap: wrap;
  overflow: auto;
  background: linear-gradient(to bottom right, var(--bgColor1), var(--bgColor2),var(--bgColor3));
  background-size: 200% 200%;
  animation: gradient 8s ease infinite;
}



.card {
  width: 50%;
  //height: 20%;
  margin-top: 200px;
}

.head {
  width: 300px;
  height: 200px;
  margin: 0 20%;
  position: relative;
}

.head img{
  width: 100%;
  position: absolute;
  transition: 0.5s;
  border-radius: 10px;
}

.cover{
  z-index: 3;
  opacity: 0;
}
.card:hover .index{
  box-shadow: 0 35px 35px -8px rgba(0,0,0,0.7);
  transform: perspective(500px) rotateX(25deg);
}

.card:hover .cover{
  opacity: 1;
  transform: perspective(500px) translate3d(0,-85px,50px);
}
.index{
  z-index: 1;
}
.title {
  z-index: 2;
  bottom: -80px;
}

.card .text{
  margin-top: 40px;
  margin-left: 18%;
}
</style>
