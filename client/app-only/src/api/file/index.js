import http from '../../net/http'

class FileService {
    constructor(){

    }

    upload(type, file){
        return http.post('/file/upload', {mediaType: type, file});
    }

    fileURL(path){
        return http.defaults.baseURL + '/file/download?path='+encodeURI(path);
    }
}

export default new FileService();