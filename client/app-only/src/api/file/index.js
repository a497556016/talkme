import http from '../../net/http'

class FileService {
    constructor(){

    }

    upload(type, file){
        return http.withLoading().post('/file/upload', {mediaType: type, file});
    }

    fileURL(path){
        const url = http.defaults.baseURL + '/file/download?path='+encodeURI(path);
        return url;
    }
}

export default new FileService();