<template>
  <view class="page-wrap">
    <MessageBell />
    <!-- 用户信息卡片 -->
    <view class="lay-card">
      <view class="flex-col align-center" style="padding: 30rpx 0">
        <view class="lay-avatar lg" @click="showAvatarUpload">
          <image v-if="profile.avatar" :src="profile.avatar" mode="aspectFill"/>
          <text v-else class="avatar-placeholder">{{ profile.nickName ? profile.nickName[0] : '?' }}</text>
        </view>
        <text class="user-name">{{ profile.nickName || '未设置昵称' }}</text>
        <text class="text-muted">{{ profile.remark || '还没有个人简介哦~' }}</text>
      </view>

      <view class="info-row">
        <text class="info-label">部门</text>
        <text>{{ deptName || '未知部门' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">手机</text>
        <text>{{ profile.telNo || '未设置' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">角色</text>
        <text>{{ userStore.state.userInfo.roles?.join(', ') || '普通用户' }}</text>
      </view>
    </view>

    <!-- Tab: 基本信息 / 账号绑定 -->
    <view class="lay-card" style="padding: 0">
      <view class="lay-tab-header">
        <view class="lay-tab-item" :class="{ active: activeTab === 'baseInfo' }" @click="activeTab = 'baseInfo'">
          基本信息
        </view>
        <view class="lay-tab-item" :class="{ active: activeTab === 'bindingInfo' }" @click="activeTab = 'bindingInfo'">
          账号绑定
        </view>
      </view>
    </view>

    <!-- 基本信息 -->
    <view class="lay-card" v-if="activeTab === 'baseInfo'">
      <view class="lay-form">
<!--        <view class="lay-form-item">-->
<!--          <text class="lay-form-label">用户名</text>-->
<!--          <input class="lay-input" :value="profile.userName" disabled-->
<!--                 style="background: #f5f5f5; color: #999"/>-->
<!--        </view>-->
        <view class="lay-form-item">
          <text class="lay-form-label required">昵称</text>
          <input class="lay-input" placeholder="请输入昵称" v-model="editForm.nickName"/>
        </view>
        <view class="lay-form-item">
          <text class="lay-form-label">性别</text>
          <picker mode="selector" :range="genderRange" @change="onGenderChange" :value="genderIndex">
            <view class="lay-select">
              <text :class="{ placeholder: !editForm.gender }">
                {{ genderDisplay || '请选择性别' }}
              </text>
              <text class="arrow">▼</text>
            </view>
          </picker>
        </view>
        <view class="lay-form-item">
          <text class="lay-form-label required">联系电话</text>
          <input class="lay-input" type="number" placeholder="请输入电话" v-model="editForm.telNo"/>
        </view>
        <view class="lay-form-item">
          <text class="lay-form-label">邮箱</text>
          <input class="lay-input" type="text" placeholder="请输入邮箱" v-model="editForm.email"/>
        </view>
        <view class="lay-form-item">
          <text class="lay-form-label">个人简介</text>
          <textarea class="lay-textarea" placeholder="还没有个人简介哦~"
                    v-model="editForm.remark" :maxlength="500"/>
          <text class="text-muted" style="font-size: 22rpx; text-align: right; display: block">
            {{ (editForm.remark || '').length }}/500
          </text>
        </view>
        <view class="lay-btn-group" style="justify-content: center; margin-top: 20rpx">
          <button class="lay-btn lay-btn-primary" @click="submitProfile">提交</button>
          <button class="lay-btn" @click="resetForm">重置</button>
        </view>
      </view>
    </view>

    <!-- 账号绑定 -->
    <view class="lay-card" v-if="activeTab === 'bindingInfo'">
      <view v-for="item in bindingList" :key="item.id" class="binding-item">
        <view class="flex-row align-center gap-sm" style="flex: 1">
          <view class="binding-icon" :style="{ backgroundColor: item.color + '20' }">
            <text :style="{ color: item.color, fontSize: '36rpx' }">{{ item.iconText }}</text>
          </view>
          <view>
            <text class="binding-title">{{ item.title }}</text>
            <text class="text-muted" style="font-size:22rpx; display:block">{{ item.desc }}</text>
          </view>
        </view>
        <text class="text-primary">{{ item.option }}</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="lay-card" style="margin-top: 30rpx">
      <button class="lay-btn lay-btn-danger lay-btn-block" @click="doLogout">退出登录</button>
    </view>

    <view style="height: 40rpx"></view>
  </view>
</template>

<script>
import {ref, reactive, onMounted, computed} from 'vue'
import {findCurrentUserProfile, saveUserInfo} from '@/api/system/User'
import {findDeptById} from '@/api/system/Dept'
import {findCacheSysDictValueList} from '@/api/system/Dict'
import {useCryptStore} from '@/store/crypt'
import {useUserStore} from '@/store/user'
import {removeToken, removeUserInfo} from '@/utils/storage'
import Http from '@/api/Http'

export default {
  name: 'Profile',
  setup() {
    const userStore = useUserStore()
    const cryptStore = useCryptStore()

    const activeTab = ref('baseInfo')
    const profile = reactive({})
    const editForm = reactive({
      nickName: '', gender: '', telNo: '', email: '', remark: ''
    })
    const deptName = ref('')
    const genderOptions = ref([])
    const genderRange = ref(['未知', '男', '女'])

    const bindingList = ref([
      {id: 1, title: '密保手机', desc: '已绑定手机', option: '去修改', iconText: '📱', color: '#009688'},
      {id: 2, title: '密保邮箱', desc: '已绑定邮箱', option: '去修改', iconText: '📧', color: '#1e9fff'},
      {id: 3, title: '密保问题', desc: '未设置密保问题', option: '去设置', iconText: '🔒', color: '#ffb800'},
      {id: 4, title: '绑定QQ', desc: '未绑定QQ账号', option: '去绑定', iconText: 'Q', color: '#3492ed'},
      {id: 5, title: '绑定微信', desc: '未绑定微信账号', option: '去绑定', iconText: 'W', color: '#4daf29'},
      {id: 6, title: '绑定微博', desc: '未绑定微博账号', option: '去绑定', iconText: 'B', color: '#e6162d'}
    ])

    const loadProfile = async () => {
      try {
        const res = await findCurrentUserProfile()
        if (res.code === 200) {
          const decrypted = await cryptStore.decryptAes(res.data)
          const data = JSON.parse(decrypted)
          Object.assign(profile, data)
          Object.assign(editForm, {
            nickName: data.nickName || '',
            gender: data.gender || '',
            telNo: data.telNo || '',
            email: data.email || '',
            remark: data.remark || ''
          })
        }
      } catch (e) {
        console.error('加载用户信息失败', e)
        uni.showToast({title: '加载用户信息失败', icon: 'none'})
      }
    }

    const loadDept = async () => {
      try {
        const res = await findDeptById()
        if (res.code === 200 && res.data) {
          deptName.value = res.data.deptName || ''
        }
      } catch (e) { /* ignore */ }
    }

    const loadGenders = async () => {
      try {
        const res = await findCacheSysDictValueList('SYS_GENDER')
        if (res.code === 200 && res.data) {
          genderOptions.value = res.data
          genderRange.value = ['请选择', ...res.data.map(d => d.valueName || d.dictLabel || d.label || '')]
        }
      } catch (e) { console.error('加载性别选项失败', e) }
    }

    const onGenderChange = (e) => {
      const idx = e.detail.value
      if (idx === 0) {
        editForm.gender = ''
      } else {
        editForm.gender = genderOptions.value[idx - 1]?.value || genderOptions.value[idx - 1]?.dictValue || genderRange.value[idx]
      }
    }

    const genderIndex = computed(() => {
      if (!editForm.gender) return 0
      const idx = genderOptions.value.findIndex(d => (d.value || d.dictValue) === editForm.gender)
      return idx >= 0 ? idx + 1 : 0
    })
    const genderDisplay = computed(() => {
      if (!editForm.gender) return ''
      const idx = genderOptions.value.findIndex(d => (d.value || d.dictValue) === editForm.gender)
      if (idx >= 0) {
        return genderOptions.value[idx]?.valueName || genderOptions.value[idx]?.dictLabel || genderOptions.value[idx]?.label || ''
      }
      return editForm.gender
    })

    const submitProfile = async () => {
      if (!editForm.nickName) {
        uni.showToast({title: '请输入昵称', icon: 'none'})
        return
      }
      if (!editForm.telNo) {
        uni.showToast({title: '请输入联系电话', icon: 'none'})
        return
      }

      uni.showLoading({title: '保存中...'})
      try {
        const payload = {
          id: profile.id,
          userName: profile.userName,
          nickName: editForm.nickName,
          gender: editForm.gender,
          telNo: editForm.telNo,
          email: editForm.email,
          remark: editForm.remark
        }
        const encrypted = await cryptStore.encryptAes(payload)
        const res = await saveUserInfo(encrypted)
        if (res.code === 200) {
          uni.showToast({title: '保存成功', icon: 'success'})
          // Update local profile
          Object.assign(profile, payload)
          if (userStore.state.userInfo) {
            userStore.state.userInfo.nickname = editForm.nickName
            userStore.state.userInfo.avatar = profile.avatar
          }
        } else {
          uni.showToast({title: res.msg || '保存失败', icon: 'none'})
        }
      } catch (e) {
        uni.showToast({title: '保存失败', icon: 'none'})
      } finally {
        uni.hideLoading()
      }
    }

    const resetForm = () => {
      editForm.nickName = profile.nickName || ''
      editForm.gender = profile.gender || ''
      editForm.telNo = profile.telNo || ''
      editForm.email = profile.email || ''
      editForm.remark = profile.remark || ''
    }

    const showAvatarUpload = () => {
      uni.showToast({title: '头像上传功能开发中', icon: 'none'})
    }

    const doLogout = () => {
      uni.showModal({
        title: '确认退出',
        content: '确定要退出登录吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await userStore.logout()
            } catch(e) { /* ignore */ }
            removeToken()
            removeUserInfo()
            uni.reLaunch({url: '/pages/login/index'})
          }
        }
      })
    }

    onMounted(() => {
      loadProfile()
      loadDept()
      loadGenders()
    })

    return {
      userStore, activeTab, profile, editForm, deptName,
      genderRange, genderIndex, genderDisplay, bindingList,
      onGenderChange, submitProfile, resetForm,
      showAvatarUpload, doLogout
    }
  }
}
</script>

<style lang="scss" scoped>
.page-wrap {
  min-height: 100vh;
}

.user-name {
  font-size: 36rpx;
  font-weight: 600;
  margin: 16rpx 0 8rpx;
  color: #333;
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  font-size: 60rpx;
  background: var(--global-primary-color);
  color: #fff;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1px solid #f5f5f5;
  font-size: 28rpx;

  &:last-child {
    border-bottom: none;
  }
}

.info-label {
  width: 80rpx;
  color: #999;
  flex-shrink: 0;
}

.binding-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.binding-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.binding-title {
  font-size: 28rpx;
  color: #333;
  display: block;
}
</style>
