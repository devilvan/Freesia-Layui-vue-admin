type CloseFunction = () => void;

const layerStack: CloseFunction[] = [];

export const pushLayer = (closeFn: CloseFunction): void => {
    layerStack.push(closeFn);
};

export const popLayer = (): CloseFunction | undefined => {
    return layerStack.pop();
};

export const closeTopLayer = (): boolean => {
    const closeFn = popLayer();
    if (closeFn) {
        closeFn();
        return true;
    }
    return false;
};

export const getLayerCount = (): number => {
    return layerStack.length;
};

export const getLayerStack = (): CloseFunction[] => {
    return layerStack;
}