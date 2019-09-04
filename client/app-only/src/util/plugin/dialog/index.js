import Dialog from './Dialog'
import Toast from './Toast'

function DialogBuilder(Vue) {
    return {
        installAlert: {
            info(msg, options){
                this.install(Object.assign({msg, dialogType: '1', msgType: '1'}, options), Dialog);
            },
            success(msg, options){
                this.install(Object.assign({msg, dialogType: '1', msgType: '2'}, options), Dialog);
            },
            error(msg, options){
                this.install(Object.assign({msg, dialogType: '1', msgType: '3'}, options), Dialog);
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
            this.install(Object.assign({msg: src, dialogType: '3'}, options), Dialog);
        },
        install(options, comp){
            const dialog = Vue.extend(comp);
            const component = new dialog({
                data: options
            }).$mount();
            document.querySelector('body').appendChild(component.$el);
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
            error: db.installAlert.error.bind(db)
        }
        Vue.prototype.$confirm = db.installConfirm.bind(db);
        Vue.prototype.$imageViewer = db.installImage.bind(db);
    }
}