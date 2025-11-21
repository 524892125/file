1. WebAssembly 是什么？

WebAssembly（简称 WASM）是一种 在浏览器里运行的二进制指令格式，特点是：

比 JavaScript 快很多（接近原生速度）

内存安全

可与 JS 互操作

跨平台：浏览器、Node、边缘计算、服务器都能跑

✅ 2. Rust 是做 WebAssembly 最强的语言之一

Rust 天生适合 WASM 因为：

内存安全

编译器能生成体积非常小的 WASM

wasm-bindgen + web-sys 可以直接调用浏览器 API

性能极强

✅ 3. Rust → WASM：最标准的开发方式

使用 wasm-pack + wasm-bindgen

📌 安装工具
# 安装 wasm32 目标
rustup target add wasm32-unknown-unknown

# 安装 wasm-pack
cargo install wasm-pack

✅ 4. 创建一个 WASM 项目
wasm-pack new wasm-demo
cd wasm-demo


项目结构：

wasm-demo/
src/
lib.rs
Cargo.toml

✅ 5. 写 Rust 代码（将来会被 JS 调用）

编辑 src/lib.rs：

use wasm_bindgen::prelude::*;

#[wasm_bindgen]
pub fn add(a: i32, b: i32) -> i32 {
a + b
}

#[wasm_bindgen]
pub fn hello(name: &str) -> String {
format!("Hello, {}!", name)
}

✅ 6. 编译成 WebAssembly
wasm-pack build --target web


输出的文件在 pkg/ 内：

pkg/
wasm_demo_bg.wasm
wasm_demo.js
wasm_demo.d.ts

✅ 7. 浏览器中使用 WASM

创建一个 index.html：

<!DOCTYPE html>
<html>
<body>
  <script type="module">
    import init, { add, hello } from "./pkg/wasm_demo.js";

    async function run() {
      await init();

      console.log(add(1, 2));        // 3
      console.log(hello("李晟"));    // Hello, 李晟!
    }

    run();
  </script>
</body>
</html>