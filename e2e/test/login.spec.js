import { browser, expect } from '@wdio/globals'

/**
 * E2E login Ady Trans Jaya.
 *
 * Semua `Modifier.testTag` di aplikasi diekspos sebagai resource-id karena
 * MainActivity menyetel `testTagsAsResourceId = true`, sehingga elemen dicari
 * lewat UiAutomator: `android=new UiSelector().resourceId("...")`.
 */

const byTag = (tag) => $(`android=new UiSelector().resourceId("${tag}")`)
const byText = (text) => $(`android=new UiSelector().textContains("${text}")`)

/** Tunggu splash (1,5 detik) selesai dan form login tampil. */
async function waitForLoginScreen() {
    const email = await byTag('login_email')
    await email.waitForDisplayed({ timeout: 30000 })
    return email
}

/**
 * Gulir layar sampai elemen dengan testTag tertentu terlihat. LoginScreen
 * memakai Column yang bisa di-scroll, jadi kartu demo di bagian bawah bisa
 * berada di luar layar pada perangkat kecil.
 */
async function scrollToTag(tag) {
    await browser.execute('mobile: scroll', {
        strategy: '-android uiautomator',
        selector: `new UiSelector().resourceId("${tag}")`,
        maxSwipes: 10,
    })
}

async function hideKeyboardIfShown() {
    try {
        await browser.hideKeyboard()
    } catch {
        // Keyboard memang tidak sedang tampil.
    }
}

async function fillCredentials(email, password) {
    const emailField = await waitForLoginScreen()

    await emailField.click()
    await emailField.setValue(email)

    const passwordField = await byTag('login_password')
    await passwordField.click()
    await passwordField.setValue(password)

    // Tutup keyboard agar tombol Masuk tidak tertutup IME.
    await hideKeyboardIfShown()
}

async function submitLogin() {
    await byTag('login_submit').click()
}

async function expectLoggedIn(roleLabel) {
    const home = await byTag('home_root')
    await home.waitForDisplayed({ timeout: 20000 })
    await expect(home).toBeDisplayed()
    await expect(byText(`Ady Trans Jaya · ${roleLabel}`)).toBeDisplayed()
}

describe('Login', () => {
    beforeEach(async () => {
        // noReset=false di wdio.conf.js: sesi baru memasang ulang APK dan
        // menghapus data lama, jadi tiap test mulai dari halaman login.
        await browser.reloadSession()
        await waitForLoginScreen()
    })

    it('menampilkan form login dan kartu akun demo', async () => {
        await expect(byTag('login_email')).toBeDisplayed()
        await expect(byTag('login_password')).toBeDisplayed()
        await expect(byTag('login_submit')).toBeDisplayed()
        await expect(byTag('go_to_register')).toExist()

        await expect(byTag('demo_owner')).toExist()
        await expect(byTag('demo_karyawan')).toExist()
        await expect(byTag('demo_customer')).toExist()
    })

    it('menolak submit saat email dan kata sandi kosong', async () => {
        await submitLogin()

        const error = await byTag('login_error')
        await error.waitForDisplayed({ timeout: 10000 })
        await expect(byText('Email dan kata sandi wajib diisi.')).toBeDisplayed()
    })

    it('menolak kata sandi yang salah', async () => {
        await fillCredentials('owner@adytransjaya.id', 'kata-sandi-salah')
        await submitLogin()

        const error = await byTag('login_error')
        await error.waitForDisplayed({ timeout: 15000 })
        await expect(byText('Email atau kata sandi salah')).toBeDisplayed()

        // Tetap berada di halaman login, belum masuk ke dashboard.
        await expect(byTag('login_email')).toBeDisplayed()
    })

    it('berhasil masuk sebagai pemilik', async () => {
        await fillCredentials('owner@adytransjaya.id', 'owner123')
        await submitLogin()

        await expectLoggedIn('Pemilik')
        await expect(byTag('tab_beranda')).toBeDisplayed()
    })

    it('berhasil masuk memakai kartu akun demo karyawan', async () => {
        await scrollToTag('demo_karyawan')
        await byTag('demo_karyawan').click()
        await submitLogin()

        await expectLoggedIn('Karyawan')
        await expect(byTag('tab_pelanggan')).toBeDisplayed()
    })

    it('menerima email tanpa memedulikan huruf besar atau kecil', async () => {
        await fillCredentials('CUSTOMER@ADYTRANSJAYA.ID', 'customer123')
        await submitLogin()

        await expectLoggedIn('Pelanggan')
    })

    it('membuka halaman registrasi dari tautan daftar', async () => {
        await scrollToTag('go_to_register')
        await byTag('go_to_register').click()

        const registerEmail = await byTag('register_email')
        await registerEmail.waitForDisplayed({ timeout: 15000 })
        await expect(registerEmail).toBeDisplayed()
        await expect(byTag('register_submit')).toExist()
    })

    it('kembali ke halaman login dari halaman registrasi', async () => {
        await scrollToTag('go_to_register')
        await byTag('go_to_register').click()

        const back = await byTag('back_to_login')
        await back.waitForDisplayed({ timeout: 15000 })
        await back.click()

        await waitForLoginScreen()
        await expect(byTag('login_email')).toBeDisplayed()
    })
})
