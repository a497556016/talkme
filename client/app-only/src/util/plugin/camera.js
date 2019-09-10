const Camera = {
    DestinationType: {
        DATA_URL: 0,
        FILE_URI: 1,
        NATIVE_URI: 2
    },
    SourceType: {
        PHOTO_LIBRARY : 0,
        CAMERA : 1,
        SAVED_PHOTO_ALBUM : 2
    },
    /**
     * Set the type of media to select from. Only works when PictureSourceType
     * is PHOTOLIBRARY or SAVEDPHOTOALBUM. Defined in nagivator.camera.MediaType
     *      PICTURE: 0      allow selection of still pictures only. DEFAULT.
     *          Will return format specified via DestinationType
     *      VIDEO: 1        allow selection of video only, WILL ALWAYS RETURN FILE_URI
     *      ALLMEDIA : 2    allow selection from all media types
     */
    MediaType: {
        PICTURE: 0,
        VIDEO: 1,
        ALLMEDIA: 2
    }
}

class CameraUtil {
    options = {
        quality: 50,
        allowEdit: true,
        destinationType: Camera.DestinationType.DATA_URL,
        sourceType: Camera.SourceType.PHOTO_LIBRARY
    }
    constructor(){

    }

    getPhoto(){
        return this.getPicture({
            sourceType: Camera.SourceType.SAVED_PHOTO_ALBUM,
            mediaType: Camera.MediaType.PICTURE
        })
    }

    takePhoto(){
        return this.getPicture({
            sourceType: Camera.SourceType.CAMERA,
            mediaType: Camera.MediaType.PICTURE
        })
    }

    takeVideo(){
        return this.getPicture({
            sourceType: Camera.SourceType.SAVED_PHOTO_ALBUM,
            mediaType: Camera.MediaType.VIDEO
        })
    }

    getPicture(cameraOptions){
        Object.assign(this.options, cameraOptions)
        return new Promise((resolve, reject) => {
            if(navigator.camera){
                navigator.camera.getPicture((data) => {
                    if(!data.startsWith("/storage")) {
                        resolve(data);
                    }else {
                        // alert(data);
                        window.resolveLocalFileSystemURL('file:///' + data, fileEntry => {
                            fileEntry.file((file) => {
                                const reader = new FileReader();
                                reader.onloadend = function(e) {
                                    const content = this.result;
                                    resolve(content);
                                };
                                // The most important point, use the readAsDatURL Method from the file plugin
                                reader.readAsDataURL(file);
                            });
                        })
                    }
                }, message => {
                    // alert(message)
                    reject(message);
                }, this.options);
            }else {
                const fileInput = document.createElement('input');
                fileInput.type = 'file';
                fileInput.accept = '';
                if(this.options.mediaType == Camera.MediaType.VIDEO){
                    fileInput.accept = 'video/*';
                }else if(this.options.mediaType == Camera.MediaType.PICTURE){
                    fileInput.accept = 'image/*';
                }
                fileInput.capture = this.options.sourceType == Camera.SourceType.CAMERA?'camera':'';
                // alert(0)
                fileInput.onchange = function (evt, f) {
                    // alert(1)
                    console.log(evt, f)
                    const file = evt.path[0].files[0];
                    const fr = new FileReader();
                    fr.onload = function (e) {
                        console.log(e)
                        resolve(e.target.result);
                        fileInput.value = '';
                    }
                    fr.readAsDataURL(file);
                }
                fileInput.click();
            }
        })
    }
}

export default new CameraUtil()