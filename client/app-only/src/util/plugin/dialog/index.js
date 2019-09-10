import Dialog from './Dialog'
import Toast from './Toast'
import LoadingMask from './LoadingMask'

function DialogBuilder(Vue) {
    return {
        installAlert: {
            info(msg, options){
                return this.install(Object.assign({msg, dialogType: '1', msgType: '1', autoClose: true}, options), Dialog);
            },
            success(msg, options){
                return this.install(Object.assign({msg, dialogType: '1', msgType: '2', autoClose: true}, options), Dialog);
            },
            error(msg, options){
                return this.install(Object.assign({msg, dialogType: '1', msgType: '3', autoClose: true}, options), Dialog);
            },
            loading(msg, options){
                return this.install(Object.assign({msg, dialogType: '1', msgType: '4'}, options), Dialog);
            }
        },
        installConfirm(options) {
            options.dialogType = '2';
            this.install(options, Dialog);
        },
        installToast(msg, options){
            this.install(Object.assign({msg}, options), Toast);
        },
        installImage(src, options){
            options = options || {};
            // Vue.prototype.$toast(Vue.prototype.isApp)
            if(Vue.prototype.isOnApp){
                PhotoViewer.show(src, options.title, {share: true})
            }else {
                this.install(Object.assign({msg: src, dialogType: '3'}, options), Dialog);
            }
        },

        installLoadingMask(msg, options){
            this.install(Object.assign({msg}, options), LoadingMask);
        },

        installVideoPlayer(src, options){
            options = options || {};
            options.msg = src;
            options.dialogType = '4';
            this.install(options, Dialog);
        },

        install(options, comp){
            const dialog = Vue.extend(comp);
            const component = new dialog({
                data: options
            }).$mount();
            document.querySelector('body').appendChild(component.$el);
            return component;
        }
    }
}

export default {
    install(Vue, options){
        const db = DialogBuilder(Vue);

        Vue.prototype.$toast = db.installToast.bind(db);
        Vue.prototype.$alert = {
            info: db.installAlert.info.bind(db),
            success: db.installAlert.success.bind(db),
            error: db.installAlert.error.bind(db),
            loading: db.installAlert.loading.bind(db)
        }
        Vue.prototype.$confirm = db.installConfirm.bind(db);
        Vue.prototype.$imageViewer = db.installImage.bind(db);

        Vue.prototype.$videoPlayer = db.installVideoPlayer.bind(db);
    }
}