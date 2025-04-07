<template>
  <div class="login-wrap">
    <div class="login-root">
      <div class="login-main">
        <img class="login-one-ball"
             src="https://assets.codehub.cn/micro-frontend/login/fca1d5960ccf0dfc8e32719d8a1d80d2.png"/>
        <img class="login-two-ball"
             src="https://assets.codehub.cn/micro-frontend/login/4bcf705dad662b33a4fc24aaa67f6234.png"/>
        <div class="login-container">
          <div class="login-side">
            <div class="login-bg-title">
              <h1>layui vue admin</h1>

              <h3 style="margin: 20px auto">
                开 箱 即 用 的 layui vue 企 业 级 前 端 模 板
              </h3>
            </div>
          </div>
          <div class="login-ID">
            <div style="font-size: 22px; margin-bottom: 15px; margin-top: 5px">
              🎯 Sign in
            </div>
            <lay-tab type="brief" v-model="method">
              <lay-tab-item title="用户名" id="1">
                <div style="height: 400px">
                  <lay-form :model="loginForm" label-position="top" ref="loginFormRef" :rules="loginFormRules"
                            @keydown.enter.prevent="loginSubmit">
                    <lay-form-item label="用户名" prop="username" required>
                      <lay-input :allow-clear="true" prefix-icon="layui-icon-username" placeholder="用户名"
                                 v-model="loginForm.username"></lay-input>
                    </lay-form-item>
                    <lay-form-item label="密码" prop="password" required>
                      <lay-input :allow-clear="false" prefix-icon="layui-icon-password" placeholder="密码" password
                                 type="password" v-model="loginForm.password"></lay-input>
                    </lay-form-item>
                    <lay-form-item label="验证码" prop="code" :hidden="!captchaEnabled"
                                   :required="!captchaEnabled">
                      <div style="width: 60%; display: inline-block">
                        <lay-input :allow-clear="true" prefix-icon="layui-icon-vercode" placeholder="验证码"
                                   v-model="loginForm.code"></lay-input>
                      </div>
                      <div class="login-captcha" @click="toRefreshImg">
                        <img style="width: 100%" :src="captchaImg" alt="获取验证码"/>
                      </div>
                    </lay-form-item>
                    <!--                    <lay-checkbox value="" name="like" v-model="remember" skin="primary" label="1">记住密码</lay-checkbox>-->
                    <lay-form-item>
                      <lay-button style="margin-top: 20px" type="primary" :loading="loging" :fluid="true"
                                  loadingIcon="layui-icon-loading" @click="loginSubmit">登录
                      </lay-button>
                    </lay-form-item>
                  </lay-form>
                </div>
              </lay-tab-item>
              <lay-tab-item title="二维码" id="2">
                <div style="width: 200px; height: 250px; margin: 0 auto">
                  <lay-qrcode text="http://www.layui-vue.com" :width="200" color="#000"
                              style="margin: 10px 0 20px"></lay-qrcode>
                  <div style="text-align: center; cursor: pointer" @click="toRefreshQrcode">
                    <lay-icon type="layui-icon-refresh-three"></lay-icon>
                    刷新二维码
                  </div>
                </div>
              </lay-tab-item>
            </lay-tab>
            <lay-line>Other login methods</lay-line>
            <ul class="other-ways">
              <li>
                <div class="line-container">
                  <img class="icon" src="@/assets/login/w.svg"/>
                  <p class="text">微信</p>
                </div>
              </li>
              <li>
                <div class="line-container">
                  <img class="icon" src="@/assets/login/q.svg"/>
                  <p class="text">钉钉</p>
                </div>
              </li>
              <li>
                <div class="line-container">
                  <img class="icon" src="@/assets/login/a.svg"/>
                  <p class="text">Gitee</p>
                </div>
              </li>
              <li>
                <div class="line-container">
                  <img class="icon" src="@/assets/login/f.svg"/>
                  <p class="text">Github</p>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import {useUserStore} from '@/store/user'
import {layer} from '@layui/layer-vue'
import {LoginVo} from "@/types/login/LoginForm";
import {login} from "@/api/Login";
import {findCaptchaEnabled} from "@/api/system/Config";
import {getCaptchaCode} from "@/api/captcha/Captcha";
import {loginQrcode} from "@/api/module/commone";
import router from "@/router";
import {useCryptStore} from "@/store/crypt";
import SvgIcon from "@/views/component/svg/SvgIcon.vue";
import AccountTypeIconPicker from "@/views/component/svg/AccountTypeIconPicker.vue";

/* INIT*/
onMounted(async () => {
  const {data, code} = await findCaptchaEnabled()
  if (code === 200) {
    if (data === true) {
      captchaEnabled.value = true;
      toRefreshImg();
    }
  }
  // toRefreshQrcode()
})
/* INIT*/

/* VAR*/
const $router = router;
const $crypt = useCryptStore();
const userStore = useUserStore()
const method = ref('1')
const captchaImg = ref('')
const loging = ref(false);
const loading = ref(false);
const loginQrcodeText = ref('')
const remember = ref(false)
const loginForm: LoginVo = reactive<LoginVo>({})
const loginFormRef = ref()
const captchaEnabled = ref(false);
const loginFormRules = ref({
  code: {
    validator(rule: { field: any; }, value: any, callback: (arg0: Error) => void) {
      if (captchaEnabled.value === true) {
        if (!value) {
          callback(new Error('验证码不能为空！'));
        } else {
          return true;
        }
      } else {
        return true;
      }
    }
  }
})

/* VAR*/

/* FUNCTION */
const loginSubmit = async () => {
  loginFormRef.value.validate(async (isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      loging.value = true;
      login(await $crypt.encryptAes(loginForm)).then((res: any) => {
        setTimeout(() => {
          loging.value = false;
          if (res.code == 200) {
            layer.msg(res.msg, {icon: 1}, async () => {
              userStore.token = res.data.token
              await userStore.getInfo()
              await router.push('/')
            })
          } else {
            toRefreshImg()
          }
        }, 100)
      }).catch(e => {
        layer.confirm(e, {icon: 2})
      })
    }
  })

}
const toRefreshImg = () => {
  // setTimeout(() => {
  getCaptchaCode().then((res: any) => {
    if (res.code == 200) {
      captchaImg.value = "data:image/gif;base64," + res.data?.captchaImg
      loginForm.captchaKey = res.data?.captchaKey;
    } else {
      layer.msg(res.msg, {icon: 2})
    }
  })
  // }, 1000)
}
const toRefreshQrcode = async () => {
  let {data, code, msg} = await loginQrcode()
  if (code == 200) {
    loginQrcodeText.value = data.data
  } else {
    layer.msg(msg, {icon: 2})
  }
}
/* FUNCTION*/
</script>

<style scoped>
.login-captcha {
  display: inline-block;
  vertical-align: bottom;
  width: 108px;
  height: 40px;
  color: var(--global-primary-color);
  margin-left: 8px;
  border-radius: 4px;
  border: 1px solid hsla(0, 0%, 60%, 0.46);
  transition: border 0.2s;
  box-sizing: border-box;
  background: #fff;
  overflow: hidden;
  cursor: pointer;
}

.login-one-ball {
  opacity: 0.4;
  position: absolute;
  max-width: 568px;
  left: -400px;
  bottom: 0px;
}

.login-two-ball {
  opacity: 0.4;
  position: absolute;
  max-width: 320px;
  right: -200px;
  top: -60px;
}

.login-wrap {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  overflow: auto;
  min-width: 600px;
  z-index: 9;
  background-image: url(https://assets.codehub.cn/micro-frontend/login/f7eeecbeccefe963298c23b54741d473.png);
  background-repeat: no-repeat;
  background-size: cover;
  min-height: 100vh;
}

.login-wrap :deep(.layui-input-block) {
  margin-left: 0 !important;
}

.login-root {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  justify-content: center;
  width: 100%;
  min-width: 320px;
  background-color: initial;
}

.login-main {
  position: relative;
  display: block;
}

.logo-container {
  max-width: calc(100vw - 28px);
  margin-bottom: 40px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-container .logo {
  display: inline-block;
  height: 30px;
  width: 143px;
  background: url() no-repeat 50%;
  background-size: contain;
  cursor: pointer;
}

.login-container {
  position: relative;
  overflow: hidden;
  width: 940px;
  height: 720px;
  max-width: calc(100vw - 28px);
  border-radius: 4px;
  background: hsla(0, 0%, 100%, 0.5);
  backdrop-filter: blur(30px);
  display: flex;
  box-shadow: 6px 6px 12px 4px rgba(0, 0, 0, 0.1);
}

.login-side {
  padding: 40px 20px 20px;
  background-color: var(--global-primary-color);
  flex: 1;
  height: 100%;
}

.login-bg-title {
  flex: 1;
  height: 84%;
  color: #fff;
  text-align: center;
  background-image: url('@/assets/login/login-bg.svg');
  background-repeat: no-repeat;
  background-position: bottom;
  background-size: contain;
  text-align: center;
  min-width: 200px;
}

.login-ID {
  padding: 20px;
  width: 380px;
  min-width: 380px;
}

.login-container .layui-tab-head {
  background: transparent;
}

.login-container .layui-input-wrapper {
  margin-top: 10px;
  margin-bottom: 10px;
}

.login-container .layui-input-wrapper {
  margin-top: 12px;
  margin-bottom: 12px;
}

.login-container .assist {
  margin-top: 5px;
  margin-bottom: 5px;
  letter-spacing: 2px;
}

.login-container .layui-btn {
  margin: 10px 0px 10px 0px;
  letter-spacing: 2px;
  height: 40px;
}

.login-container .layui-line-horizontal {
  letter-spacing: 2px;
  margin-bottom: 34px;
  margin-top: 24px;
}

.other-ways {
  display: flex;
  justify-content: space-between;
  margin: 0;
  padding: 20px;
  list-style: none;
  font-size: 14px;
  font-weight: 400;
}

.other-ways li {
  width: 100%;
}

.line-container {
  justify-content: center;
  align-items: center;
  text-align: center;
  cursor: pointer;
}

.line-container .icon {
  height: 28px;
  width: 28px;
  margin-right: 0;
  vertical-align: middle;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 1px 2px 0 rgb(9 30 66 / 4%), 0 1px 4px 0 rgb(9 30 66 / 10%),
  0 0 1px 0 rgb(9 30 66 / 10%);
}

.line-container .text {
  display: block;
  margin: 12px 0 0;
  font-size: 12px;
  color: #8592a6;
}

:deep(.layui-tab-title .layui-this) {
  background-color: transparent;
}
</style>
