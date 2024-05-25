```vue
<template>
    <div class="image-upload">
        <h1>图片上传和转换为黑白简笔画</h1>
        <input type="file" @change="onFileChange" accept="image/*" />
        <div v-if="originalImage">
            <h2>原图</h2>
            <img :src="originalImage" alt="Original Image" />
        </div>
        <div v-if="processedImage">
            <h2>黑白简笔画</h2>
            <img :src="processedImage" alt="Processed Image" />
        </div>
    </div>
</template>

<script>
export default {
    name: 'JianBIhua',
    data() {
        return {
            originalImage: null,
            processedImage: null,
        };
    },
    methods: {
        onFileChange(event) {
            const file = event.target.files[];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    this.originalImage = e.target.result;
                    this.convertToSketch(this.originalImage);
                };
                reader.readAsDataURL(file);
            }
        },
        convertToSketch(imageSrc) {
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');
            const img = new Image();
            img.src = imageSrc;
            img.onload = () => {
                canvas.width = img.width;
                canvas.height = img.height;
                ctx.drawImage(img, , );

                // Create OpenCV Mat from canvas
                let src = cv.imread(canvas);
                let dst = new cv.Mat();
                cv.cvtColor(src, src, cv.COLOR_RGBA2GRAY, );
                cv.Canny(src, dst, 50, 100, 3, false);

                // Invert colors to get white background and black lines
                cv.bitwise_not(dst, dst);

                // Convert the result back to canvas
                cv.cvtColor(dst, dst, cv.COLOR_GRAY2RGBA, );
                cv.imshow(canvas, dst);

                this.processedImage = canvas.toDataURL();

                // Clean up
                src.delete();
                dst.delete();
            };
        },
    },
};
</script>

<style scoped>
.image-upload {
    text-align: center;
}

img {
    max-width: 100%;
    height: auto;
    margin-top: 20px;
}
</style>


// 使用
<script async src="https://docs.opencv.org/3.4.0/opencv.js" onload="onOpenCvReady();"></script>
<script>
    function onOpenCvReady() {
        console.log('OpenCV.js is ready.');
    }
</script>
```