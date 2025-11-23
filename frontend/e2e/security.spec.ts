import { test, expect } from '@playwright/test';

test.describe('Security - XSS Protection', () => {

  test('ska rendera potentiell XSS i album-namn utan att köra skript', async ({ page }) => {
    const xssPayload = '<script>alert("XSS!")</script>';
    const harmlessTitle = 'Harmless Album';

    // Lyssna efter dialog-rutor
    page.on('dialog', dialog => {
      throw new Error(`Oväntad dialogruta: ${dialog.message()}`);
    });

    // Mocka API-svaret för att inkludera en XSS-payload
    await page.route('**/api/releases', route => {
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          { id: 1, artist: 'Attacker', title: xssPayload, coverUrl: 'url1', year: 2023, genre: 'Rock' },
          { id: 2, artist: 'Benign', title: harmlessTitle, coverUrl: 'url2', year: 2023, genre: 'Pop' }
        ])
      });
    });

    // Mocka genre-anropet så att sidan kan laddas
    await page.route('**/api/genres', route => {
        route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify(['Rock', 'Pop'])
        });
    });

    await page.goto('/');

    // Verifiera att album-korten renderas
    await expect(page.locator('app-release-card')).toHaveCount(2);

    // Verifiera att XSS-payloaden visas som text och inte exekveras.
    const cardWithPayload = page.locator('app-release-card', { hasText: xssPayload });
    await expect(cardWithPayload).toBeVisible();

    // Dubbelkolla att den ofarliga titeln också finns där
    await expect(page.getByText(harmlessTitle)).toBeVisible();
  });
});
