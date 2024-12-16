const svgIconsInstall = (Vue) => {
    const svgRequire = require.context('./svg', false, /\.svg$/)
    svgRequire.keys().forEach((svgIcon) => svgRequire(svgIcon))
}


export default {
    svgIconsInstall
}