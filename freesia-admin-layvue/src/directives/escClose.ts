// src/directives/escToClose.ts
import { Directive, DirectiveBinding } from 'vue';
import { pushLayer, closeTopLayer, getLayerStack } from '@/directives/layerStack';

interface EscDirectiveElement extends HTMLElement {
    _escHandler?: () => void;
}

const layerStack = getLayerStack()

const EscToCloseDirective: Directive = {
    mounted(el: EscDirectiveElement, binding: DirectiveBinding<() => void>) {
        if (typeof binding.value !== 'function') {
            console.warn('[v-esc-to-close] Expected a function as value');
            return;
        }

        const closeFn = () => {
            binding.value();
            // Clean up from stack if not already removed
            const index = layerStack.findIndex(fn => fn === closeFn);
            if (index !== -1) {
                layerStack.splice(index, 1);
            }
        };

        pushLayer(closeFn);

        // Store the closeFn reference for cleanup
        el._escHandler = closeFn;

        // Add global keydown listener if not already present
        if (!document.__escLayerListener) {
            const handleKeydown = (e: KeyboardEvent) => {
                if (e.key === 'Escape' && layerStack.length > 0) {
                    e.preventDefault();
                    closeTopLayer();
                }
            };

            document.addEventListener('keydown', handleKeydown);
            document.__escLayerListener = handleKeydown;
        }
    },
    unmounted(el: EscDirectiveElement) {
        // Clean up the close function from stack when component unmounts
        if (el._escHandler) {
            const index = layerStack.findIndex(fn => fn === el._escHandler);
            if (index !== -1) {
                layerStack.splice(index, 1);
            }
            delete el._escHandler;
        }
    }
};

declare global {
    interface Document {
        __escLayerListener?: (e: KeyboardEvent) => void;
    }
}

export default EscToCloseDirective;