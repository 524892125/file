mod utils;

use wasm_bindgen::prelude::*;
use base64::{engine::general_purpose, Engine};
use js_sys::Date;

#[wasm_bindgen]
extern "C" {
    fn alert(s: &str);
}


static KEY_XOR: u8 = 0x5A;

// "R5dMWWIhIH0JRRtpIwiwPHphh}Hc)J}QhHKWthYpIxtx}IKN"
static KEY_DATA: &[u8] = &[
    0x08,0x6F,0x3E,0x17,0x0D,0x0D,0x13,0x32,0x13,0x12,0x6A,0x10,0x08,0x08,
    0x2E,0x2A,0x13,0x2D,0x33,0x2D,0x0A,0x12,0x2A,0x32,0x32,0x27,0x12,0x39,
    0x73,0x10,0x27,0x0B,0x32,0x12,0x11,0x0D,0x2E,0x32,0x03,0x2A,0x13,0x22,
    0x2E,0x22,0x27,0x13,0x11,0x14
];

pub fn load_key() -> String {
    KEY_DATA.iter()
        .map(|b| b ^ KEY_XOR)
        .collect::<Vec<u8>>()
        .into_iter()
        .map(|c| c as char)
        .collect()
}

// XOR + Base64 Encode
#[wasm_bindgen]
pub fn xor_encrypt_base64(text: &str) -> String {
    let key = load_key();
    let t = text.as_bytes();
    let k = key.as_bytes();

    let mut out = Vec::with_capacity(t.len());

    for (i, &b) in t.iter().enumerate() {
        out.push(b ^ k[i % k.len()]);
    }

    general_purpose::STANDARD.encode(out) + "_" + &key
}

// XOR + Base64 Encode
#[wasm_bindgen]
pub fn t_e() -> String {
    // 获取当前毫秒时间戳
    let ts = Date::now().to_string();
    let key = load_key();
    let t = ts.as_bytes();
    let k = key.as_bytes();

    let mut out = Vec::with_capacity(t.len());

    for (i, &b) in t.iter().enumerate() {
        out.push(b ^ k[i % k.len()]);
    }

    general_purpose::STANDARD.encode(out)
}


// XOR + Base64 Decode
#[wasm_bindgen]
pub fn t_d(b64: &str) -> String {
    let key = load_key();

    let raw = general_purpose::STANDARD
        .decode(b64)
        .expect("invalid base64");

    let k = key.as_bytes();

    let mut out = Vec::with_capacity(raw.len());

    for (i, &b) in raw.iter().enumerate() {
        out.push(b ^ k[i % k.len()]);
    }

    String::from_utf8(out).expect("invalid utf8")
}