// cypress/e2e/admin/admin-delete-track.cy.ts

describe('Admin deletes a track', () => {
    beforeEach(() => {
        cy.loginAs('admin')
        cy.visit('/tracks')
    })

    it('removes a track from the list', () => {
        // Get first track name
        cy.get('[data-testid="track-name"]').first().then(($el) => {
            const name = $el.text()

            // Delete the track
            cy.get('[data-testid="delete-button"]').first().click()

            // Ensure it is removed
            cy.contains(name).should('not.exist')
        })
    })
})
