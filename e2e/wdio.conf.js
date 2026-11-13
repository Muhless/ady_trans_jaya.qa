import fs from 'node:fs'
import path from 'node:path'
import { execFileSync, spawn } from 'node:child_process'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const projectRoot = path.resolve(__dirname, '..')

const APPIUM_HOST = process.env.APPIUM_HOST ?? '127.0.0.1'
const APPIUM_PORT = Number(process.env.APPIUM_PORT ?? 4723)
const APPIUM_STATUS_URL = `http://${APPIUM_HOST}:${APPIUM_PORT}/status`

const APK_PATH =
    process.env.APK_PATH ??
    path.join(projectRoot, 'app/build/outputs/apk/debug/app-debug.apk')
const DEVICE_NAME = process.env.ANDROID_DEVICE ?? 'pixel_api36'
const PLATFORM_VERSION = process.env.ANDROID_VERSION ?? '16'

let appiumProcess = null

function adb(args) {
    return execFileSync('adb', args, { encoding: 'utf8' })
}

function connectedDevices() {
    return adb(['devices'])
        .split('\n')
        .slice(1)
        .map((line) => line.trim())
        .filter((line) => line.endsWith('device'))
}

async function isAppiumUp() {
    try {
        const response = await fetch(APPIUM_STATUS_URL)
        return response.ok
    } catch {
        return false
    }
}

async function waitForAppium(timeoutMs) {
    const deadline = Date.now() + timeoutMs
    while (Date.now() < deadline) {
        if (await isAppiumUp()) return
        await new Promise((resolve) => setTimeout(resolve, 500))
    }
    throw new Error(
        `Server Appium tidak merespons di ${APPIUM_HOST}:${APPIUM_PORT} setelah ${timeoutMs} ms.`,
    )
}

export const config = {
    runner: 'local',
    hostname: APPIUM_HOST,
    port: APPIUM_PORT,
    path: '/',
    specs: ['./test/**/*.spec.js'],
    maxInstances: 1,
    capabilities: [
        {
            platformName: 'Android',
            'appium:automationName': 'UiAutomator2',
            'appium:deviceName': DEVICE_NAME,
            'appium:platformVersion': PLATFORM_VERSION,
            'appium:app': APK_PATH,
            'appium:appPackage': 'com.adytransjaya.app',
            'appium:appActivity': 'com.adytransjaya.app.MainActivity',
            // Mulai dari state bersih: data Room & sesi SharedPreferences dihapus
            // sehingga seed awal selalu berjalan dan hasil test deterministik.
            'appium:noReset': false,
            'appium:autoGrantPermissions': true,
            'appium:newCommandTimeout': 180,
            'appium:uiautomator2ServerInstallTimeout': 180000,
            'appium:uiautomator2ServerLaunchTimeout': 180000,
            // Animasi Compose (splash/infinite transition) bisa membuat
            // UiAutomator2 menunggu idle terlalu lama.
            'appium:settings[waitForIdleTimeout]': 0,
        },
    ],
    logLevel: 'warn',
    bail: 0,
    waitforTimeout: 15000,
    connectionRetryTimeout: 180000,
    connectionRetryCount: 1,
    framework: 'mocha',
    reporters: ['spec'],
    mochaOpts: {
        ui: 'bdd',
        timeout: 180000,
    },

    async onPrepare() {
        if (!fs.existsSync(APK_PATH)) {
            throw new Error(
                `APK tidak ditemukan di ${APK_PATH}. Jalankan dulu:\n` +
                    '  ./gradlew :app:assembleDebug',
            )
        }

        const devices = connectedDevices()
        if (devices.length === 0) {
            throw new Error(
                'Tidak ada emulator/perangkat Android yang terhubung.\n' +
                    'Nyalakan emulator dulu, misalnya:\n' +
                    '  ~/Android/Sdk/emulator/emulator -avd pixel_api36 &\n' +
                    '  adb wait-for-device',
            )
        }
        console.log(`[e2e] Perangkat terdeteksi: ${devices.join(', ')}`)
        console.log(`[e2e] APK: ${APK_PATH}`)

        if (await isAppiumUp()) {
            console.log(`[e2e] Memakai server Appium yang sudah berjalan di ${APPIUM_HOST}:${APPIUM_PORT}`)
            return
        }

        console.log(`[e2e] Menjalankan server Appium di ${APPIUM_HOST}:${APPIUM_PORT}...`)
        appiumProcess = spawn(
            'appium',
            ['--address', APPIUM_HOST, '--port', String(APPIUM_PORT), '--log-level', 'warn'],
            { stdio: 'inherit' },
        )
        appiumProcess.on('error', (error) => {
            console.error('[e2e] Gagal menjalankan perintah "appium":', error.message)
        })
        await waitForAppium(60000)
    },

    onComplete() {
        if (appiumProcess && !appiumProcess.killed) {
            console.log('[e2e] Menghentikan server Appium...')
            appiumProcess.kill('SIGTERM')
            appiumProcess = null
        }
    },
}
