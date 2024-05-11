<template>
  <iframe frameborder="false" @load="handleLoad" src="https://my.spline.design/particles-09c66dceb707e50fcd2c245d149afdc6/" style="height: 100%;width: 100%"></iframe>
  <div v-if="iframeLoaded=false" class="loading">
    正在加载3D立绘中...可直接点击按钮进入主页面
  </div>
  <div class="container" @click="router().push({ path: '/app' })" >
    <a data-animation="ripple">开启 <span style="font-weight: bold;">AI之旅</span></a>
  </div>
</template>

<script>
  import router from "@/router";
  import store from "@/store";
  import {h, onMounted} from "vue";
  import {ElNotification} from "element-plus";

  export default {
    name: `PassView`,
    data() {
      return {
        iframeLoaded: false
      }
    },
    methods: {
      router() {
        return router
      },
      handleLoad(){
        this.iframeLoaded = true;
      }
    },
    computed: {
      store() {
        return store
      }
    },
    setup() {
      const open1 = () => {
        ElNotification({
          title: 'AI绘画2.0已上新',
          message: h('i', { style: 'color: grey' }, '支持在线绘画且上传和浏览绘画作品了，快来试试吧~'),
        })
      }

      onMounted(() => {
        open1()
      })
    }
  }

</script>

<style scoped lang="scss">
.banner{
  position: absolute;
  top: 0;
  width: 100%;
  height: 600px;
  left: 50%;
  margin-left: -600px;
}

.banner .content {
  position: relative;
  top: 20%;
  display: flex;
  flex-direction: column;
  color: #fff;
}

.container {
  position: absolute;
  top: 50%;
  left: 0%;
  right: 0;
  bottom: 0;
  height: 50px;
  width: 200px;
  margin: auto;
  overflow: hidden; /* 非常重要 */
}

.container:after {
  content: '';
  display: block;
  position: absolute;
  top: -120px;
  left: -80px;
  width: 36px;
  height: 360px;
  background: #fff;
  opacity: 0.16;
  transform: rotate(-45deg);
  transition: all 500ms ease-out;
}

.container:hover:after{
  left: 200%;
}
*[data-animation="ripple"] {
  height: 100%;
  width: 100%;
  display: block;
  outline: none;
  padding: 20px;
  color: #fff;
  text-transform: uppercase;
  background: linear-gradient(135deg, #d770e7 0%, #8166e7 100%);
  box-sizing: border-box;
  text-align: center;
  line-height: 14px;
  font-family: roboto, helvetica;
  font-weight: 200;
  letter-spacing: 1px;
  text-decoration: none;
  box-shadow: 0 5px 3px rgba(0, 0, 0, 0.3);
  cursor: pointer;
  /*border-radius: 50px;*/
  -webkit-tap-highlight-color: transparent;
  border-radius: 5px;
}

*[data-animation="ripple"]:focus {
  outline: none;
}

*[data-animation="ripple"]::selection {
  background: transparent;
  pointer-events: none;
}

.loading{
  position: absolute;
  color: #8166e7;
  top: -50%;
  left: 0%;
  right: 0;
  bottom: 0;
  height: 50px;
  width: 200px;
  margin: auto;
}

</style>
