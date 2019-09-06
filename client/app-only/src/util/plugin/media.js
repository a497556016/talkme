import Vue from 'vue'

class MediaRecorder {
    path;//文件路径


    constructor(fileName, callback) {
        if(!Vue.prototype.isOnApp) {
            alert('设备不支持！');
            return;
        }

        if(device.platform == "iOS") {
            this.path = cordova.file.tempDirectory + fileName;
        } else if(device.platform == "Android") {
            this.path = cordova.file.externalRootDirectory + fileName;
        }

        this.mediaRecorder = new Media(this.path, () => {
            callback();
        });
    }


    //开始录制
    startRecord() {
        if(this.mediaRecorder) {
            this.mediaRecorder.startRecord();
        }
    }

    //停止录制
    stopRecord() {
        if(this.mediaRecorder){
            this.mediaRecorder.stopRecord();
        }
    }

    //获取音频文件
    getBase64Data() {
        return new Promise((resolve, reject) => {
            window.resolveLocalFileSystemURL(this.path, fileEntry => {
                fileEntry.file((file) => {
                    const reader = new FileReader();
                    reader.onloadend = function(e) {
                        const content = this.result;
                        resolve(content);
                    };
                    // The most important point, use the readAsDatURL Method from the file plugin
                    reader.readAsDataURL(file);
                });
            }, error => {
                reject(error);
            })
        })
    }


    //播放
    play() {
        if(this.mediaRecorder){
            this.mediaRecorder.play();
        }
    }

    //暂停
    pause() {
        if(this.mediaRecorder){
            this.mediaRecorder.pause();
        }
    }

    //停止
    stop() {
        if(this.mediaRecorder){
            this.mediaRecorder.stop();
        }
    }
}

export default MediaRecorder