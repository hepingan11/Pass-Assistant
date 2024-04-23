<template>

  <div class="body">
    <div>
      <img :src="src" style="width:100%;height:50%">
    </div>
    目前PC版显示可能有些问题，建议缩小网页或使用手机端
    <br/>
    正向提示词(英文更准确~):
    <el-input
        type="textarea"
        :rows="2"
        placeholder="请输入绘画提示词"
        v-model="prompt">
    </el-input>
    <br/>
    <br/>
    绘画模型:
    <el-select v-model="value" placeholder="请选择">
      <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value">
      </el-option>
    </el-select>
    <br/>
    <br/>
    采样器:
    <el-select v-model="value2" placeholder="请选择">
      <el-option
          v-for="item in options2"
          :key="item.value2"
          :label="item.label2"
          :value="item.value2">
      </el-option>
    </el-select>
    <br/>
    <br/>
    高度:
    <el-select v-model="height" placeholder="请选择">
      <el-option
          v-for="item in shape"
          :key="item.height"
          :label="item.label3"
          :value="item.height">
      </el-option>
    </el-select>
    <br/>
    <br/>
    长度:
    <el-select v-model="width" placeholder="请选择">
      <el-option
          v-for="item in shape2"
          :key="item.width"
          :label="item.label3"
          :value="item.width">
      </el-option>
    </el-select>
    <br/>
    <br/>
    步数:
    <el-select v-model="step" placeholder="请选择">
      <el-option
          v-for="item in options3"
          :key="item.step"
          :label="item.label3"
          :value="item.step">
      </el-option>
    </el-select>
    <br/>
    <br/>

    <el-button type="primary" @click="requestImage">开始绘画</el-button>
<!--    <el-button type="primary" @click="scrollToBottom">\/</el-button>-->

    <br/>
    (画好后记得保存到本地哦~)
    <br/>
    <div v-if="loading===1">
      <h1>
        <span>加</span>
        <span>载</span>
        <span>中</span>
        <span>.</span>
        <span>.</span>
        <span>.</span>
      </h1>
      (预计需要1分钟，请勿刷新网页或点击按钮)
    </div>
    <div v-if="loading===2">
      <div v-if="decodedImages.length">
        <div v-for="(img, index) in decodedImages" :key="index">
          <img :src="img" alt="image" />
        </div>
      </div>
      <el-button type="primary" @click="uploadImage">上传图片</el-button>
    </div>
    <div v-if="loading===3">
      <div class="milky">AI绘画调用失败T_T</div>
      <el-button type="primary" @click="redirectTo">绘画错误，点我去另一个绘画接口</el-button>
    </div>
    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
  </div>
<!--  <div class="demo-image__placeholder">-->
<!--    <div class="block">-->
<!--      <span class="demonstration"></span>-->
<!--      <el-image :src="src"></el-image>-->
<!--    </div>-->
<!--  </div>-->



</template>

<script>

import axios from "axios";
// import LeftNavigationBar from "@/components/LeftNavigationBar.vue";

export default {
  name: "DrawingView",
  // components: {LeftNavigationBar},
  data() {
    return {
      src: 'https://img2.imgtp.com/2024/03/17/3qkbcd2h.png',
      textarea: '',
      loading: '0',
      decodedImages: [],
      prompt: 'lovely girl',
      step: '',

      options: [{
        value: 'mixProV4.Cqhm.safetensors',
        label: '经典模型'
      }, {
        value: 'Anime_Art_SDXL.safetensors',
        label: '优美动漫风格'
      }, {
        value: 'majicMIX realistic_v6.safetensors',
        label: '写真人物'
      }, {
        value: 'Counterfeit-V2.5_v30.safetensors',
        label: '丰富二次元'
      }],
      value: '',
      options2: [{
        value2: 'Euler a',
        label2: 'Euler a(默认推荐)'
      }, {
        value2: 'Euler',
        label2: 'Euler'
      }, {
        value2: 'LMS',
        label2: 'LMS'
      }, {
        value2: 'DPM++ 2M',
        label2: 'DPM++ 2M'
      }],

      options3: [{
        step: '10',
        label3: '10'
      }, {
        step: '20',
        label3: '20'
      }, {
        step: '30',
        label3: '30'
      }, {
        step: '40',
        label3: '40'
      }],
      height: '512',
      width: '512',
      shape: [{
        height: 512,
        label3: "512 px"
      },{
        height: 720,
        label3: "720 px"
      },{
        height: 1080,
        label3: "1080 px"
      },{
        height: 1980,
        label3: "1980 px"
      },{
        height: 2560,
        label3: "2560 px"
      }],
      shape2: [{
        width: 512,
        label3: "512 px"
      },{
        width: 720,
        label3: "720 px"
      },{
        width: 1080,
        label3: "1080 px"
      },{
        width: 1980,
        label3: "1980 px"
      },{
        width: 2560,
        label3: "2560 px"
      }]
    }
  },
  methods: {
    scrollToBottom() {
      window.scrollTo({
        top: document.documentElement.scrollHeight,
        behavior: 'smooth'  // 平滑滚动
      });
    },
    redirectTo() {
      window.location.href = "https://sd.hepingan.top";
    },
    requestImage() {
      console.log("开始发送请求");
      this.loading=1;
      //TODO
      axios.post("https://sd.fc-stable-diffusion-plus.1398817069756357.cn-hangzhou.fc.devsapp.net/sdapi/v1/txt2img", {
        "prompt": this.prompt,
        "step": this.step,
        "height": this.height,
        "width": this.width,
        "override_settings": {
          "sd_model_checkpoint": this.value,
        },
      }, {
        headers: {
          "Authorization": "Basic Og=="
        }
      }).then(response => {
        console.log(response.status);
        this.loading = 2;
        this.decodeImages(response.data.images); // 解码并展示图像数据
        // this.costMoney();
      }).catch(err => {
        console.error(err);
        this.loading = 3;
      });
    },
    decodeImages(images) {
      const decodedImages = images.map(img => this.base64Decode(img));
      this.decodedImages = decodedImages;
    },
    base64Decode(base64) {
      const binaryString = window.atob(base64);
      const len = binaryString.length;
      const bytes = new Uint8Array(len);
      for (let i = 0; i < len; ++i) {
        bytes[i] = binaryString.charCodeAt(i);
      }
      const blob = new Blob([bytes.buffer], { type: 'image/png' });
      return URL.createObjectURL(blob); // 将解码后的图像数据转换为URL
    },
    costMoney(){
      axios.post("http://localhost:8625/drawing/sd/gpt/text"
      ).then(re =>{
        console.log(re.status)
      }).catch(err =>{
        console.log(err);
      })
    },
    async uploadImage() {
      const formData = new FormData();
      this.decodedImages.forEach((item) => formData.append('file', item.file));
      const res = await axios.post("https://www.imgtp.com/api/upload",formData,{
        headers:{
          "token": "34ea3eb2efcfb085b6af1032058dbb45",
          "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0"
        }
      });
      if (res.status === 200){
        console.log("上传成功");
        return "上传成功";
      }else {
        console.log("上传失败");
        return "上传失败";
      }
    }

  }

};
</script>

<style lang="scss" scoped>
:deep(.el-textarea) {
  .el-textarea__inner {
    background: var(--bgColor2);
    box-shadow: none;
    max-height: 400px;
    padding: 10px;
    font-size: 10px;
    color: var(--textColor2);

    &:hover {
      box-shadow: none;
      background: var(--bgColor2);
    }
  }

  ::placeholder {
    color: var(--textColor4); /* 将颜色值修改为您想要的占位符文字颜色 */
  }

  /* 兼容性处理 */
  :-ms-input-placeholder {
    color: var(--textColor4); /* 将颜色值修改为您想要的占位符文字颜色 */
  }
}


h1 span {
  top: 20px; /*字符上下浮动*/
  position: relative;
  animation: loading 0.3s ease 0s infinite alternate; /*引用动画*/
  /* animation: 动画名称 动画时间 动画曲线 何时开始 播放次数 是否反方向;
  alternate:表示动画先正常运行再反方向运行，并持续交替
  infinite:表示无限循环*/
  font-size: 100px;
  color: white;
  text-shadow: 0 1px 0  #CCC,
  0 2px 0 #CCC,
  0 3px 0 #CCC,
  0 4px 0 #CCC,
  0 5px 0 #CCC,
  0 6px 0 #CCC,
  0 7px 0 #CCC,
  0 8px 0 #CCC,
  0 9px 0 #CCC,
  0 10px 10px rgba(0, 0, 0, 0.5);
  /*text-shadow: 水平阴影位置(必须) 垂直阴影位置(必须) 模糊距离(可选) 阴影颜色(可选);
    text-shadow: h-shadow v-shadow blur color; */
}
h1 span:nth-child(2) {
  animation-delay: 0.1s;
}
h1 span:nth-child(3) {
  animation-delay: 0.2s;
}
h1 span:nth-child(4) {
  animation-delay: 0.3s;
}
h1 span:nth-child(5) {
  animation-delay: 0.4s;
}
h1 span:nth-child(6) {
  animation-delay: 0.5s;
}
@keyframes loading {  /* 定义动画*/
  100% {
    top: -20px;  /*字符上下浮动*/
    text-shadow: 0 1px 0 #CCC,
    0 2px 0 #CCC,
    0 3px 0 #CCC,
    0 4px 0 #CCC,
    0 5px 0 #CCC,
    0 6px 0 #CCC,
    0 7px 0 #CCC,
    0 8px 0 #CCC,
    0 9px 0 #CCC,
    0 50px 25px rgba(0, 0, 0, 0.3);
    /*上升的时候阴影要在文字下面一些，所以垂直阴影的位置坐标和模糊距离要大一些*/
  }
}

.milky{
  font-size: 80px;   /*设置文字大小*/
  color:#3366FF;  /*设置文字颜色*/
  text-shadow: 0 8px 10px #6699FF;  /*设置文字阴影*/
  font-weight: bolder;  /*设置文字宽度*/
  text-align: center;  /*设置文字居中*/

}

.img {
  max-width: 100%;
  height: auto;
}
.body {
  max-width: 640px;
  min-width: 320px;
  margin: 0 auto;


  /* overflow: hidden; */
  overflow-x: hidden;
}


</style>
