import http from '../../net/http'

class FileService {
    constructor(){

    }

    upload(type, file){
        return http.withLoading('正在上传...').post('/file/upload', {mediaType: type, file});
    }

    fileURL(path){
        const url = http.defaults.baseURL + '/file/download?path='+encodeURI(path);
        return url;
    }

    base64(path){
        return http.get('/file/base64', {
            params: {
                path: encodeURI(path)
            }
        })
    }
}

export default new FileService();