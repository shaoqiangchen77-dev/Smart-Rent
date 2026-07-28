<template>
  <view class="edit-page">
    <AppNavbar :show-back="true" title="编辑资料" />

    <!-- 头像 -->
    <view class="avatar-section" @click="chooseAvatar">
      <image v-if="avatarPreview" :src="avatarPreview" class="avatar-img" />
      <view v-else class="avatar-img avatar-ph">
        <text>{{ initial }}</text>
      </view>
      <view class="avatar-meta">
        <text class="avatar-title">头像</text>
        <text class="avatar-tip">{{ uploading ? '上传中…' : '点击更换头像' }}</text>
      </view>
      <SrIcon class="avatar-arrow" name="chev" :size="30" color="#9a9183" />
    </view>

    <view class="form-section">
      <!-- 昵称 -->
      <view class="form-row">
        <view class="ic"><SrIcon name="user" :size="36" color="#b08a3a" /></view>
        <text class="lbl">昵称</text>
        <view class="ctrl">
          <input v-model="nickname" placeholder="请输入昵称" placeholder-class="input-ph" class="form-input" maxlength="20" />
        </view>
      </view>

      <!-- 邮箱 -->
      <view class="form-row">
        <view class="ic"><SrIcon name="send" :size="36" color="#b08a3a" /></view>
        <text class="lbl">邮箱</text>
        <view class="ctrl">
          <input v-model="email" placeholder="请输入邮箱" placeholder-class="input-ph" class="form-input" />
        </view>
      </view>

      <!-- 手机号（只读，账号即手机号） -->
      <view class="form-row">
        <view class="ic"><SrIcon name="phone" :size="36" color="#b08a3a" /></view>
        <text class="lbl">手机号</text>
        <view class="ctrl">
          <text class="form-input readonly">{{ phoneMasked }}</text>
        </view>
      </view>
    </view>

    <button class="save-btn" :loading="saving" @click="onSave">
      <text>保存修改</text>
    </button>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import AppNavbar from '@/components/app-navbar.vue'
import SrIcon from '@/components/SrIcon.vue'
import { useUserStore } from '@/store/user'
import { updateUserInfo } from '@/api/user'

const userStore = useUserStore()

const nickname = ref('')
const email = ref('')
const avatarPreview = ref(userStore.userInfo?.avatar || '')
const pendingAvatar = ref('') // 上传成功后待保存的 url
const uploading = ref(false)
const saving = ref(false)

const initial = computed(() => {
  const n = nickname.value || userStore.userInfo?.username || '客'
  return n.slice(0, 1)
})
const phoneMasked = computed(() => maskPhone(userStore.userInfo?.phone || ''))

onMounted(() => {
  const u = userStore.userInfo
  if (u) {
    nickname.value = u.nickname || u.username || ''
    email.value = u.email || ''
  }
})

// 手机号脱敏：中间四位用 **** 替换
function maskPhone(phone: string): string {
  if (!phone || phone.length < 7) return phone || '—'
  return phone.slice(0, 3) + '****' + phone.slice(-4)
}

// 选择并上传头像
function chooseAvatar() {
  if (uploading.value) return
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    success: async (res) => {
      const temp = res.tempFilePaths[0]
      avatarPreview.value = temp // 本地预览
      uploading.value = true
      try {
        const url = await uploadAvatar(temp)
        pendingAvatar.value = url
        uni.showToast({ title: '头像已选择', icon: 'none' })
      } catch (e: any) {
        uni.showToast({ title: (e && e.message) || '头像上传失败', icon: 'none' })
        avatarPreview.value = userStore.userInfo?.avatar || '' // 还原
      } finally {
        uploading.value = false
      }
    },
  })
}

// 上传到 /api/file/upload，返回 url
function uploadAvatar(filePath: string): Promise<string> {
  // #ifdef MP-WEIXIN
  const base = 'http://localhost:8080/api'
  // #endif
  // #ifndef MP-WEIXIN
  const base = '/api'
  // #endif
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: base + '/file/upload',
      filePath,
      name: 'file',
      formData: { dir: 'avatar' },
      header: { Authorization: userStore.token },
      success: (r) => {
        try {
          const json = JSON.parse(r.data)
          if (json.code === 200 && json.data && json.data.url) resolve(json.data.url)
          else reject(new Error(json.msg || '上传失败'))
        } catch {
          reject(new Error('上传解析失败'))
        }
      },
      fail: (err) => reject(new Error((err && err.errMsg) || '上传失败')),
    })
  })
}

async function onSave() {
  if (saving.value) return
  saving.value = true
  try {
    const data: Record<string, string> = {}
    if (nickname.value && nickname.value.trim()) data.nickname = nickname.value.trim()
    if (email.value && email.value.trim()) data.email = email.value.trim()
    if (pendingAvatar.value) data.avatar = pendingAvatar.value
    await updateUserInfo(data)
    await userStore.fetchUserInfo()
    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 600)
  } catch (e: any) {
    uni.showToast({ title: (e && e.message) || '保存失败', icon: 'none' })
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.edit-page {
  min-height: 100vh;
  background: var(--bg);
}
.avatar-section {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin: 24rpx;
  padding: 30rpx 28rpx;
  background: var(--glass);
  border: 1rpx solid var(--line);
  border-radius: 32rpx;
  box-shadow: var(--shadow);
}
.avatar-img {
  width: 104rpx;
  height: 104rpx;
  border-radius: 50%;
  background: var(--glass-2);
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.avatar-img image,
.avatar-img .avatar-ph {
  width: 100%;
  height: 100%;
}
.avatar-ph {
  font-size: 40rpx;
  font-weight: 700;
  color: var(--gold-2);
}
.avatar-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}
.avatar-title {
  font-size: 30rpx;
  font-weight: 700;
  color: var(--txt);
}
.avatar-tip {
  font-size: 24rpx;
  color: var(--txt-3);
}
.avatar-arrow {
  flex: 0 0 auto;
  transform: rotate(180deg);
}
.form-section {
  padding: 0 24rpx;
}
.form-row {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: var(--glass);
  border: 1rpx solid var(--line);
  border-radius: 30rpx;
  padding: 26rpx;
  box-shadow: var(--shadow);
  margin-bottom: 20rpx;
}
.form-row .ic {
  width: 56rpx;
  height: 56rpx;
  border-radius: 18rpx;
  background: var(--glass-2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
}
.form-row .lbl {
  font-size: 28rpx;
  color: var(--txt);
  font-weight: 600;
  flex: 0 0 auto;
  width: 110rpx;
}
.form-row .ctrl {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  min-width: 0;
}
.form-input {
  flex: 1;
  text-align: right;
  font-size: 28rpx;
  background: transparent;
  color: var(--txt);
}
.form-input.readonly {
  color: var(--txt-2);
}
.input-ph {
  color: var(--txt-3);
}
.save-btn {
  width: calc(100% - 48rpx);
  height: 88rpx;
  line-height: 88rpx;
  background: var(--grad);
  color: #2a2113;
  font-size: 32rpx;
  font-weight: 700;
  border-radius: 16rpx;
  border: none;
  margin: 30rpx 24rpx 0;
  box-shadow: 0 12rpx 24rpx rgba(176, 138, 58, 0.2);
  letter-spacing: 4rpx;
}
.save-btn:active {
  transform: scale(0.97);
}
</style>
