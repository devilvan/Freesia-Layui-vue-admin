// stores/modalStore.ts
import {defineStore} from 'pinia';

export const useModalStore = defineStore('modal', {
    state: () => ({
        modalStack: [] as string[], // 存储弹窗的唯一标识（如组件名或 ID）
    }),
    actions: {
        openModal(modalId: string) {
            console.log(modalId)
            this.modalStack.push(modalId);
        },
        closeModal() {
            if (this.modalStack.length > 0) {
                this.modalStack.pop();
            }
        },
    },
});