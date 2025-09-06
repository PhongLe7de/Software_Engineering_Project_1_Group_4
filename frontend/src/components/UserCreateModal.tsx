import React, { useState } from "react"

import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { toast } from "sonner"
import { z } from "zod"

import { Button } from "@/components/ui/button"
import {
    Card,
    CardContent,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import {
    Carousel,
    CarouselContent,
    CarouselItem,
    CarouselNext,
    CarouselPrevious,
    type CarouselApi,
} from "@/components/ui/carousel"
import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"

type User = {
    displayName: string;
    photoUrl: string;
}

type LoginModalProps = {
    activateSidebar: (show: boolean) => void;
    setUserData: (userData: { displayName: string; photoUrl: string; }) => void;
};

const FormSchema = z.object({
    displayName: z.string().min(2, {
        message: "Username must be at least 2 characters.",
    }),
})

// TODO: Placeholder layout, project part 2: user auth and oAuth 2.0(?)
export default function UserCreateModal({ activateSidebar, setUserData }: LoginModalProps) {
    const [api, setApi] = useState<CarouselApi>()
    const [selectProfilePic, setSelectProfilePic] = useState(0)

    // TODO: Emojis are placeholder. Final ver.: File uploads and default profile pics
    const avatars = ["🦧", "👷"]
    const form = useForm<z.infer<typeof FormSchema>>({
        resolver: zodResolver(FormSchema),
        defaultValues: {
            displayName: "",
        },
    })

    const createUser = async (userData: User) => {
        try {
            const response = await fetch(`${import.meta.env.API_URL}user/profile`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData),
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const newUser = await response.json();
            return newUser;
        } catch (error) {
            console.error('Error creating user:', error);
            throw error;
        }
    }

    const handleLogin = async (data: z.infer<typeof FormSchema>) => {
        try {
            const userData: User = {
                displayName: data.displayName,
                photoUrl: avatars[selectProfilePic]
            };

            const newUser = await createUser(userData);

            toast.success(`Welcome ${data.displayName}!`);
            setUserData(newUser);
            console.log(newUser);
            activateSidebar(true);
        } catch (e) {
            toast.error("Failed to create user. Please try again.");
        }
    }

    // shadcn carousel api listener
    React.useEffect(() => {
        if (!api) return

        const onSelect = () => {
            setSelectProfilePic(api.selectedScrollSnap())
        }

        api.on("select", onSelect)
        onSelect()

        return () => {
            api.off("select", onSelect)
        }
    }, [api])

    return (
        <Card className="fixed top-4 left-1/2 transform -translate-x-1/2 flex flex-col w-full max-w-sm z-50">
            <CardHeader>
                <CardTitle>Log in</CardTitle>
            </CardHeader>
            <CardContent>
                <Carousel className="max-w-xs" setApi={setApi}>
                    <CarouselContent>
                        {avatars.map((avatar, index) => (
                            <CarouselItem key={index} className="flex aspect-square items-center justify-center p-6">
                                <span className="text-9xl">{avatar}</span>
                            </CarouselItem>
                        ))}
                    </CarouselContent>
                    <CarouselPrevious />
                    <CarouselNext />
                </Carousel>

            </CardContent>
            <CardFooter className="flex-col gap-2">
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(handleLogin)} className="w-2/3 space-y-6">
                        <FormField
                            control={form.control}
                            name="displayName"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Display name</FormLabel>
                                    <FormControl>
                                        <Input placeholder="Bob" {...field} />
                                    </FormControl>
                                    <FormDescription>
                                        This is your public display name.
                                    </FormDescription>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <Button className="w-full" type="submit">Submit {avatars[selectProfilePic]}</Button>
                    </form>
                </Form>
            </CardFooter>
        </Card>
    )
}

