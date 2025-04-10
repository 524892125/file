<template>
    <a-modal :maskClosable="false" width="90%" okText="导入" v-model:visible="props.visible" @cancel="handleCancel" @ok="handleOk">
        <template #title>
            批量导入
        </template>
        <div>
            <div style="margin: 0 auto;" id="example" ref="example"></div>
            <div>
                <a-button class="mt-[10px]" type="primary" @click="validItem">校验</a-button>
                <a-button class="mt-[10px] ml-[10px]" type="primary" @click="exportExcel">导出为csv</a-button>
            </div>
        </div>
    </a-modal>
    <!--    <a-button @click="getData">获取数据</a-button>-->
</template>

<script setup>
import Handsontable from 'handsontable';
import "handsontable/dist/handsontable.full.css";
import 'handsontable/styles/ht-theme-main.css';
import {onMounted, ref} from "vue";
import tippy from 'tippy.js';
import 'tippy.js/dist/tippy.css';
import {concatElements, getDuplicateIndexesGrouped} from "@/utils/common.js";
const example = ref()
const hot = ref()
const visible = ref(false);
const emit = defineEmits( ['ok', 'cancel', 'edit', 'valid'])
// 用于保存所有创建的 tippy 实例
const tippyInstances = ref([])

const props = defineProps({
    colHeaders: {
        type: Array,
        require: true,
    },
    defaultData: {
        type: Array,
        require: true,
        default: () => {
            return ['']
        }
    },
    customOption: {
        type: Object,
        default: () => {
            return {}
        }
    },
    visible: {
        type: Boolean,
        default: false
    }
})


function getData () {
    return hot.value.getData()
}

function setRowColor (row, color = 'red-color') {
    // 设置第2行（row = 1）为红色
    // const rowIndex = 1;
    const colCount = hot.value.countCols();

    for (let col = 0; col < colCount; col++) {
        hot.value.setCellMeta(row, col, 'className', color);
    }

    hot.value.render(); // 记得 render 刷新表格
}

function clearAllRowColor () {
    const rowCount = hot.value.countRows();
    const colCount = hot.value.countCols();

    for (let row = 0; row < rowCount; row++) {
        for (let col = 0; col < colCount; col++) {
            hot.value.setCellMeta(row, col, 'className', null);
        }
    }

    hot.value.render(); // 刷新表格显示
}


function addTip(row, tip) {
    const tdList = document.querySelectorAll(`.htCore tbody tr:nth-child(${+row + 1}) td`);

    tdList.forEach(td => {
        const instance = tippy(td, {
            placement: 'top',
            arrow: true,
            onShow(inst) {
                inst.setContent(tip);
            }
        });
        tippyInstances.value.push(instance);
    });
}

function clearAllTips() {
    tippyInstances.value.forEach(inst => inst?.destroy());
    tippyInstances.value = []; // 清空引用，防止内存泄漏
}

function handleCancel () {
    emit('cancel')
}

function handleOk () {
    emit('ok')
}
function open () {
    if (hot.value) {
        hot.value.destroy()
    }

    visible.value = true

    const data = [
        ['A1', 'B1', 'C1', 'D1', 'E1'],
        ['A2', 'B2', 'C2', 'D2', 'E2'],
    ];

    const colHeaders = [
        'ID',
        'Full name',
    ]

    /**
     *  columns: [
     *     {
     *       data: 0,
     *       type: 'text'  // Name 列为文本类型
     *     },
     *     {
     *       data: 1,
     *       type: 'numeric'  // Age 列为数字类型
     *     },
     *     {
     *       data: 2,
     *       type: 'date',  // Date 列为日期类型
     *       dateFormat: 'YYYY-MM-DD',  // 日期格式
     *       correctFormat: true  // 确保输入正确格式的日期
     *     }
     *   ],
     * @type {Handsontable}
     */
    hot.value = new Handsontable(example.value, {
        data: props.defaultData,
        colHeaders: props.colHeaders, // default true
        search: true,
        rowHeaders: true,
        dropdownMenu: true,
        height: '70vh',
        autoWrapRow: true,
        autoWrapCol: true,
        licenseKey: 'non-commercial-and-evaluation',
        columnSorting: true, // 列排序
        sortIndicator: true, // 显示列排序箭头
        manualRowResize: true, // 行高调整
        manualColumnResize: true, // 列宽调整
        manualRowMove: true, // 行移动
        manualColumnMove: true, // 列移动
        contextMenu: {
            items: {
                row_above: {
                    name: '在上方插入一行'
                },
                row_below: {
                    name: '在下方插入一行'
                },
                remove_row: {
                    name: '删除行'
                },
                col_left: {
                    name: '在左侧插入一列'
                },
                col_right: {
                    name: '在右侧插入一列'
                },
                remove_col: {
                    name: '删除列'
                },
                undo: {
                    name: '撤销'
                },
                redo: {
                    name: '重做'
                },
                make_read_only: {
                    name: '设为只读|取消只读'
                },
                alignment: {
                    name: '对齐方式',
                    subMenu: {
                        left: '左对齐',
                        center: '居中对齐',
                        right: '右对齐',
                        justify: '两端对齐'
                    }
                },
                cut: {
                    name: '剪切'
                },
                copy: {
                    name: '复制'
                },
                paste: {
                    name: '粘贴'
                }
            }
        },
        afterChange: function(changes, source) {
            // changes: 数据变更的详细信息
            // source: 修改源，可能是 'edit'（用户编辑），'undo'（撤销），'paste'（粘贴）等
            if (source === 'edit') {
                emit('edit')
                // changes.forEach(change => {
                //     const [row, col, oldValue, newValue] = change;
                //     console.debug(`Row: ${row}, Col: ${col}, Old Value: ${oldValue}, New Value: ${newValue}`);
                // });
            }
        },
        ...props.customOption,
    })
}

async function validItem () {
    emit('valid', JSON.parse(JSON.stringify(hot.value.getData())))
}

function exportExcel () {
    const instance = hot.value
    const data = instance.getData()
    const header = instance.getColHeader()
    const headerArr = []
    header.forEach(item => {
        headerArr.push(item)
    })
    const exportData = [headerArr]
    data.forEach(item => {
        exportData.push(item)
    })

    const csv = exportData.map(row => row.join(',')).join('\n');

    // 创建 Blob 对象并下载 CSV 文件
    const blob = new Blob([csv], { type: 'text/csv' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'export.csv';
    link.click();
}

function loadData (data) {
    hot.value.loadData(data)
}

function setDuplicateRows (...indexes) {
    let data = concatElements(hot.value.getData(), ...indexes)

    let duplicateIndexesGrouped = getDuplicateIndexesGrouped(data)
    if (Object.values(duplicateIndexesGrouped).length === 0) {
        return
    }
    for (let i of Object.values(duplicateIndexesGrouped)) {
        for (let index3 of i) {
            setRowColor(index3)
            addTip(index3, i.map(item => item + 1).join(',') + "行重复")
        }
    }
}

/**
 * {
 *         colHeaders: newHeaders
 *     }
 * @param option
 */
function updateSetting (option) {
    hot.value.updateSettings(option);
}

defineExpose({open, getData, setRowColor, addTip, loadData, clearAllTips, clearAllRowColor, setDuplicateRows, updateSetting})
</script>

<style>
td.red-color {
    color: #fff !important;
    background-color: #e03e2d !important;
}
</style>