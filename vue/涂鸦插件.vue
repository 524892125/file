<template>
    <div>
        <div class="image-doodle">
            <div class="img-box" :style="{width: width + 'px', height: height + 'px', margin: '0 auto'}" @click="handleUpload" @dragover.prevent @drop="handleDrop">
                <div v-show="!imageLoaded">
                    <div>
                        <svg data-v-064d625d="" width="52" height="52" viewBox="0 0 52 52" fill="none" xmlns="http://www.w3.org/2000/svg"><path data-v-064d625d="" d="M42.25 6.5H9.75C7.95508 6.5 6.5 7.95508 6.5 9.75V42.25C6.5 44.045 7.95508 45.5 9.75 45.5H42.25C44.045 45.5 45.5 44.045 45.5 42.25V9.75C45.5 7.95508 44.045 6.5 42.25 6.5Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"></path><path data-v-064d625d="" d="M19.5 24.9167C22.4916 24.9167 24.9167 22.4915 24.9167 19.5C24.9167 16.5085 22.4916 14.0833 19.5 14.0833C16.5085 14.0833 14.0834 16.5085 14.0834 19.5C14.0834 22.4915 16.5085 24.9167 19.5 24.9167Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"></path><path data-v-064d625d="" d="M30.106 28.4043C30.9903 27.2208 32.7739 27.2499 33.619 28.4619L44.9999 43.5001C44.9999 44.5001 43.4999 45.5001 42.4999 45.5001H17.3334L30.106 28.4043Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"></path></svg>
                    </div>
                    <div>将图像拖到这里上传 或 点我选择图片</div>
                    <div>请勿上传裸露、暴力、血腥或其他包含非法信息图片</div>
                </div>
                <div v-show="imageLoaded">
                    <canvas ref="canvas" :width="width" :height="height" v-show="imageLoaded"></canvas>
                </div>
            </div>

            <input ref="fileInput" style="display: none;" type="file" @change="onFileChange" />

        </div>
        <div :style="{width: width + 'px'}" v-if="imageLoaded" class="sd-mt">
            <el-button @click="undo"><i class="el-icon-refresh-left" />撤销</el-button>
            <el-button @click="clearCanvas">清除所有涂鸦</el-button>
            <!--                    <el-button @click="getDoodleContent">获取涂鸦内容</el-button>-->
            <!--            <button @click="downloadImage">下载图片</button>-->
            <!--                    <button @click="getBase64Image">获取Base64</button>-->
            <el-button @click="deleteImage"><i class="el-icon-delete-solid" />删除图片</el-button>
            <div style="display: inline-block;">
                <!--                画笔大小:-->
                <div style="display: flex; justify-content: center; align-items: center;">
                    <el-tooltip content="画笔大小" placement="top" effect="dark">
                        <i style="margin-left: 10px;" class="el-icon-edit" />
                    </el-tooltip>
                    <el-slider style="display: inline-block; width: 200px; margin-left: 15px;"  :min="0" :max="100" v-model="brushSize" />
                </div>
            </div>
            <label>
                <!--                画笔颜色:-->
                <!--                <input type="color" v-model="brushColor" />-->
                <!--                <el-color-picker v-model="brushColor"></el-color-picker>-->
            </label>
        </div>
    </div>
</template>

<script>
import {base64ToFile} from "@/utils/utils";

export default {
    props: {
        brushColor: {
            type: String,
            default: '#fff'
        },
        brushSize: {
            type: Number,
            default: 0
        }
    },
    data() {
        return {
            width: 800,
            height: 400,
            isDrawing: false,
            context: null,
            image: new Image(),
            imageLoaded: false,
            history: [],
            historyIndex: -1,
            doodleCanvas: null, // 新增变量
            doodleContext: null, // 新增变量
            doodleHistory: [],
            doodleHistoryIndex: -1,
            originImage: ''
        };
    },
    methods: {
        handleUpload () {
            if (this.imageLoaded) return false
            this.$refs.fileInput.click()
        },
        onFileChange(e) {
            const file = e.target.files[0];
            this.loadImage(file)
        },
        loadImage(file) {
            if (file) {
                const reader = new FileReader();
                reader.onload = (event) => {
                    this.image.onload = () => {
                        // 获取图片原始宽高比
                        const ratio = this.image.naturalWidth / this.image.naturalHeight;
                        // 根据最大宽度计算新的宽度和高度，保持宽高比
                        let newWidth, newHeight;
                        if (this.image.naturalWidth > this.image.naturalHeight) {
                            // 宽度较大时，以最大宽度为基准
                            newWidth = this.width;
                            newHeight = newWidth / ratio;
                        } else {
                            // 高度较大时，以最大高度为基准
                            newHeight = this.height;
                            newWidth = newHeight * ratio;
                        }

                        // 高度不能超过400
                        newHeight = Math.min(newHeight, 400);

                        // 更新canvas的宽高
                        const canvas = this.$refs.canvas;
                        canvas.width = newWidth;
                        canvas.height = newHeight;

                        // 获取2D上下文并清除之前的内容
                        this.context = canvas.getContext('2d');
                        this.context.clearRect(0, 0, newWidth, newHeight);

                        // 绘制等比例缩放的图片
                        this.context.drawImage(this.image, 0, 0, newWidth, newHeight);

                        // 设置imageLoaded为true，表示图片已加载并绘制到canvas上
                        this.imageLoaded = true;
                        this.saveHistory();
                    };
                    this.image.src = event.target.result;
                    this.originImage = event.target.result;
                    // console.log('origin', this.originImage)
                    this.updateLoadImage(event.target.result);
                };
                reader.readAsDataURL(file);
            }
        },


        /**
         * 重新设置图片大小
         */
        resizeImage (width = 300) {
            this.width = width
            this.$nextTick(() => {
                let file = this.$refs.fileInput.files[0];
                // console.log('file', file)
                this.loadImage(file)
            })
        },
        clearCanvas() {
            if (this.context) {
                let file = base64ToFile(this.originImage, 'a')
                this.loadImage(file)
                // this.context.clearRect(0, 0, this.width, this.height);
                // this.context.drawImage(this.image, 0, 0, this.width, this.height);
                this.resetHistory();
            }
            // 同时清除doodleCanvas上的内容
            if (this.doodleContext) {
                this.doodleContext.clearRect(0, 0, this.width, this.height);
                this.doodleHistory = [];
                this.doodleHistoryIndex = -1;
            }
        },
        getDoodleContent() {
            // 获取绘画内容
            const doodleCanvas = document.createElement('canvas');
            doodleCanvas.width = this.width;
            doodleCanvas.height = this.height;
            const doodleContext = doodleCanvas.getContext('2d');

            // 将doodleCanvas的内容复制到新的canvas上
            doodleContext.drawImage(this.doodleCanvas, 0, 0);

            // 导出新canvas的内容
            const image = doodleCanvas.toDataURL('image/png');
            console.log('tuya', image)
            return image;
        },
        undo() {
            if (this.historyIndex > 0) {
                this.historyIndex--;
                const previousImageData = this.history[this.historyIndex];
                this.context.putImageData(previousImageData, 0, 0);
            }

            // Undo changes on doodleContext
            if (this.doodleHistoryIndex > 0) {
                this.doodleHistoryIndex--;
                const previousDoodleImageData = this.doodleHistory[this.doodleHistoryIndex];
                this.doodleContext.putImageData(previousDoodleImageData, 0, 0);
            }
        },
        saveHistory() {
            this.history = this.history.slice(0, this.historyIndex + 1);
            this.history.push(this.context.getImageData(0, 0, this.width, this.height));
            this.historyIndex++;

            // Save the state of doodleContext
            this.doodleHistory = this.doodleHistory.slice(0, this.doodleHistoryIndex + 1);
            this.doodleHistory.push(this.doodleContext.getImageData(0, 0, this.width, this.height));
            this.doodleHistoryIndex++;
        },
        resetHistory() {
            this.history = [];
            this.historyIndex = -1;

            // Reset doodleHistory
            this.doodleHistory = [];
            this.doodleHistoryIndex = -1;
        },
        startDrawing(e) {
            this.isDrawing = true;
            this.context.beginPath();
            this.doodleContext.beginPath(); // Start a new path on doodleContext
            this.context.moveTo(e.offsetX, e.offsetY);
            this.doodleContext.moveTo(e.offsetX, e.offsetY); // Move to starting point on doodleContext
        },
        draw(e) {
            if (!this.isDrawing) return;
            this.context.lineTo(e.offsetX, e.offsetY);
            this.context.strokeStyle = this.brushColor;
            this.context.lineWidth = this.brushSize;
            this.context.stroke();

            // Perform the same operations on doodleContext
            this.doodleContext.lineTo(e.offsetX, e.offsetY);
            this.doodleContext.strokeStyle = this.brushColor;
            this.doodleContext.lineWidth = this.brushSize;
            this.doodleContext.stroke();
        },
        stopDrawing() {
            if (this.isDrawing) {
                this.isDrawing = false;
                this.context.closePath();
                this.doodleContext.closePath(); // Close the path on doodleContext
                this.saveHistory();
            }
        },
        downloadImage() {
            const canvas = this.$refs.canvas;
            const image = canvas.toDataURL('image/png');
            const link = document.createElement('a');
            link.href = image;
            link.download = 'doodle.png';
            link.click();
        },
        getBase64Image() {
            if (!this.originImage) {
                return false
            }
            const canvas = this.$refs.canvas;
            const imageBase64 = canvas.toDataURL('image/png');
            console.log(imageBase64); // 或者处理Base64字符串，比如通过事件发射出去
            return imageBase64
            // this.$emit('imageBase64', imageBase64);
        },
        getOriginDoodle() {
            if (this.doodleCanvas) {
                // Create a new canvas for exporting doodle content
                const doodleCanvas = document.createElement('canvas');
                doodleCanvas.width = this.width;
                doodleCanvas.height = this.height;
                const doodleContext = doodleCanvas.getContext('2d');

                // Draw doodle content onto the new canvas
                doodleContext.drawImage(this.doodleCanvas, 0, 0, this.width, this.height);

                // Export as base64 image
                return doodleCanvas.toDataURL('image/png');
            }
            return null;
        },
        // 获取涂鸦内容，跟图片原数据尺寸一致
        getDoodleContentSizeByOrigin() {
            if (!this.originImage) {
                return null;
            }

            const doodleCanvas = document.createElement('canvas');
            doodleCanvas.width = this.image.width; // 设置目标尺寸
            doodleCanvas.height = this.image.height; // 设置目标尺寸
            const doodleContext = doodleCanvas.getContext('2d');

            // 计算缩放比例
            const scaleX = this.image.width / this.$refs.canvas.width; // 原始doodleCanvas的宽度为400
            const scaleY = this.image.height / this.$refs.canvas.height; // 原始doodleCanvas的高度为400

            // 将doodleCanvas的内容等比缩放并绘制到新的canvas上
            doodleContext.scale(scaleX, scaleY);
            doodleContext.drawImage(this.doodleCanvas, 0, 0);

            // 获取缩放后的图像数据
            const image = doodleCanvas.toDataURL('image/png');
            console.log('tuya', image);
            return image;
        },
        deleteImage() {
            this.originImage = ''
            this.imageLoaded = false;
            this.image.src = '';
            if (this.context) {
                this.context.clearRect(0, 0, this.width, this.height);
            }
            this.resetHistory();
        },
        handleDrop(event) {
            // 阻止浏览器默认的拖拽行为
            event.preventDefault();

            // 处理拖拽的文件
            const file = event.dataTransfer.files[0];
            this.loadImage(file)
        },
        resetWidthAndHeight (width, height, isDelImage = true) {
            if (isDelImage) {
                this.deleteImage()
            }
            this.width = width
            this.height = height
        },
        getOriginImage () {
            // console.log(this.originImage)
            return this.originImage
        },
        updateLoadImage (originImage) {
            this.$emit('updateLoadImage', originImage)
        }
    },
    mounted() {
        const canvas = this.$refs.canvas;
        canvas.addEventListener('mousedown', this.startDrawing);
        canvas.addEventListener('mousemove', this.draw);
        canvas.addEventListener('mouseup', this.stopDrawing);
        canvas.addEventListener('mouseout', this.stopDrawing);

        // 初始化doodleCanvas和doodleContext
        this.doodleCanvas = document.createElement('canvas');
        this.doodleCanvas.width = this.width;
        this.doodleCanvas.height = this.height;
        this.doodleContext = this.doodleCanvas.getContext('2d');
    },
    beforeDestroy() {
        const canvas = this.$refs.canvas;
        canvas.removeEventListener('mousedown', this.startDrawing);
        canvas.removeEventListener('mousemove', this.draw);
        canvas.removeEventListener('mouseup', this.stopDrawing);
        canvas.removeEventListener('mouseout', this.stopDrawing);
    },
};
</script>

<style scoped>
.image-doodle {
    display: flex;
//flex-direction: column;
    align-items: center;
}

.img-box{
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    border: 1px solid #1c1d29;
    background: #1c1d29;
    cursor: pointer;
}

canvas {
    border: 1px solid #000;
}
</style>
