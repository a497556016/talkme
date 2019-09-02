const Camera = {
    DestinationType: {
        DATA_URL: 0,
        FILE_URI: 1,
        NATIVE_URI: 2
    },
    SourceType: {
        PHOTOLIBRARY : 0,
        CAMERA : 1,
        SAVEDPHOTOALBUM : 2
    }
}

class CameraUtil {
    options = {
        quality: 50,
        destinationType: Camera.DestinationType.DATA_URL,
        sourceType: Camera.SourceType.PHOTOLIBRARY
    }
    constructor(){

    }

    getPhoto(){
        return this.getPicture({
            sourceType: Camera.SourceType.PHOTOLIBRARY
        })
    }

    takePhoto(){
        return this.getPicture({
            sourceType: Camera.SourceType.CAMERA
        })
    }

    getPicture(cameraOptions){
        return new Promise((resolve, reject) => {
            if(navigator.camera){
                navigator.camera.getPicture((data) => {
                    resolve(data);
                }, message => {
                    alert(message)
                    reject(message);
                }, Object.assign(this.options, cameraOptions))
            }else {
                reject('不支持相机模式');
            }
        })
    }
}

export default new CameraUtil()