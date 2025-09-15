import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach} from 'vitest';
import UserCreateModal from '../components/UserCreateModal';



vi.mock('@faker-js/faker', () => ({
    faker: {
        internet: {
            username: () => 'test-user',
            email: () => 'test-user@example.com',
        },
    },
}));

// placeholder instead of the actual carousel
vi.mock('@/components/ui/carousel', () => ({
    Carousel: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
    CarouselContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
    CarouselItem: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
    CarouselNext: () => <button>Next</button>,
    CarouselPrevious: () => <button>Previous</button>,
}));

describe('UserCreateModal', () => {
    //  mock functions for the props passed to the component.
    const activateSidebar = vi.fn();
    const setUserData = vi.fn();

    beforeEach(() => {
        vi.clearAllMocks();
        global.fetch = vi.fn();
    });

    it('should create a user successfully on form submission', async () => {
        const user = userEvent.setup(); // Set up user-event for simulating user interactions.
        const mockUserData = { user_id: 1, display_name: 'JohnDoe', photo_url: '🦧' };

        (global.fetch as vi.Mock).mockResolvedValue({
            ok: true,
            json: () => Promise.resolve(mockUserData),
        });

        render(<UserCreateModal activateSidebar={activateSidebar} setUserData={setUserData} />);

        // Simulate a user typing a display name into the input field.
        const displayNameInput = screen.getByLabelText('Display name');
        await user.clear(displayNameInput);
        await user.type(displayNameInput, 'JohnDoe');

        // simulate submit button
        const submitButton = screen.getByRole('button', { name: /submit/i });
        await user.click(submitButton);

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledTimes(1);
            expect(global.fetch).toHaveBeenCalledWith(
                expect.stringContaining('api/auth/register'), // Check if the URL is correct.
                expect.objectContaining({ // Check if the request options are correct.
                    method: 'POST',
                    body: JSON.stringify({
                        email: 'test-user@example.com',
                        password: 'password',
                        display_name: 'JohnDoe',
                        photo_url: '🦧',
                    }),
                })
            );
        });

        await waitFor(() => {
            expect(setUserData).toHaveBeenCalledWith(mockUserData);
            expect(activateSidebar).toHaveBeenCalledWith(true);
        });
    });

    it('should show an error message if user creation fails', async () => {
        const user = userEvent.setup();
        (global.fetch as vi.Mock).mockResolvedValue({
            ok: false,
            status: 500,
        });

        render(<UserCreateModal activateSidebar={activateSidebar} setUserData={setUserData} />);

        // Simulate user input.
        const displayNameInput = screen.getByLabelText('Display name');
        await user.clear(displayNameInput);
        await user.type(displayNameInput, 'JohnDoe');

        const submitButton = screen.getByRole('button', { name: /submit/i });
        await user.click(submitButton);

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledTimes(1);
            expect(setUserData).not.toHaveBeenCalled();
            expect(activateSidebar).not.toHaveBeenCalled();
        });
    });
});