export class LicenceDownloadController {
    private static readonly BASE = '/api/licences'

    private static getAuthHeader(): Record<string, string> {
        const token = localStorage.getItem('beatshop_jwt')
        return token ? { Authorization: `Bearer ${token}` } : {}
    }

    static async downloadLicence(purchaseId: number): Promise<{ data: Blob; filename: string }> {
        const res = await fetch(`${this.BASE}/${purchaseId}/download`, {
            headers: this.getAuthHeader(),
        })
        if (!res.ok) {
            throw new Error(`Download licence failed: ${res.status}`)
        }
        const data = await res.blob()
        const disp = res.headers.get('Content-Disposition') || ''
        const filename = disp.split('filename=')[1]?.replace(/"/g, '') || `licence_${purchaseId}.pdf`
        return { data, filename }
    }
}
