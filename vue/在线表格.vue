<template>
<!--    npm install handsontable-->
    <div id="example" ref="example"></div>
</template>

<script setup>
import Handsontable from 'handsontable';
import 'handsontable/styles/handsontable.css';
import 'handsontable/styles/ht-theme-main.css';
import {onMounted, ref} from "vue";
const example = ref()

onMounted(() => {
    const data = [
        ['', 'Tesla', 'Volvo', 'Toyota', 'Ford'],
        ['2019', 10, 11, 12, 13],
        ['2020', 20, 11, 14, 13],
        ['2021', 30, 15, 12, 13],
    ];

    new Handsontable(example.value, {
        data,
        rowHeaders: true,
        colHeaders: true,
        height: 'auto',
        autoWrapRow: true,
        autoWrapCol: true,
        licenseKey: 'non-commercial-and-evaluation',
    });
})
</script>