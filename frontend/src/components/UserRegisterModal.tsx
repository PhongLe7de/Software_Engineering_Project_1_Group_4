import React, {useState} from "react"

import {zodResolver} from "@hookform/resolvers/zod"
import {useForm} from "react-hook-form"
import {toast} from "sonner"
import {z} from "zod"
import {faker} from '@faker-js/faker';

import {Button} from "@/components/ui/button"
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle,} from "@/components/ui/card"
import {
    Carousel,
    type CarouselApi,
    CarouselContent,
    CarouselItem,
    CarouselNext,
    CarouselPrevious,
} from "@/components/ui/carousel"
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage,} from "@/components/ui/form"
import {Input} from "@/components/ui/input"
import { Label } from "@/components/ui/label"

type User = {
    displayName: string;
    photoUrl: string;
    email: string;
    password: string;
}


type LoginModalProps = {
    activateSidebar: (show: boolean) => void;
    setUserData: (userData: { userId: number, email: string, displayName: string; photoUrl: string; }) => void;
};

const FormSchema = z.object({
    email: z.email({
        message: "Please enter a valid email address.",
    }),
    displayName: z.string().min(2, {
        message: "Username must be at least 2 characters.",
    }),
    password: z.string().min(5, {
        message: "Password must be at least 5 characters.",
    })
})

// TODO: Placeholder layout, project part 2: user auth and oAuth 2.0(?)
export default function UserRegisterModal({activateSidebar, setUserData}: LoginModalProps) {
    const [api, setApi] = useState<CarouselApi>()
    const [selectProfilePic, setSelectProfilePic] = useState(0)
    const [toggleBetweenRegisterLogin, setToggleBetweenRegisterLogin] = useState(false)

    // TODO: Emojis are placeholder. Final ver.: File uploads and default profile pics
    const avatars = ["🦧", "👷"]
    const registerForm = useForm<z.infer<typeof FormSchema>>({
        resolver: zodResolver(FormSchema),
        defaultValues: {
            email: faker.internet.email(), // TODO: Remove these for prod
            displayName: faker.internet.username(),
            password: "password"
        },
    })
    const loginForm = useForm<z.infer<typeof FormSchema>>({
        resolver: zodResolver(FormSchema),
        defaultValues: {
            email: "", // TODO: Remove these for prod
            displayName: "",
            password: ""
        },
    })

    const createUser = async (userData: User) => {
        const response = await fetch(`${import.meta.env.VITE_API_URL}api/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        return await response.json();
    }

    const handleRegister = async (data: z.infer<typeof FormSchema>) => {
        try {
            const userData: User = {
                email: data.email,
                password: data.password,
                displayName: data.displayName,
                photoUrl: avatars[selectProfilePic],
            };
            const newUser = await createUser(userData);
            toast.success(`Welcome ${data.displayName}!`);
            setUserData(newUser);
            console.log("userData set: ", newUser);
            activateSidebar(true);
        } catch (e) {
            if (e instanceof Error) {
                toast.error(e.message);
            } else {
                toast.error("An unknown error occurred during registration.");
            }
            console.error("Error creating user:", e);
        }
    }

    const handleLogin = async (data: z.infer<typeof FormSchema>) => {}


    // shadcn carousel api listener
    React.useEffect(() => {
        if (!api) return
        console.log(import.meta.env.VITE_API_URL);
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
        <Card className="fixed top-30 left-1/2 transform -translate-x-1/2 flex flex-col w-full max-w-sm z-50">

            {/* ------ REGISTER MODAL ------ */}

            {toggleBetweenRegisterLogin && (
                <>
                <CardHeader className="flex flex-col text-xl">
                    <CardTitle>Register</CardTitle>
                </CardHeader>
                <CardContent>
                    <Carousel className="max-w-xs" setApi={setApi}>
                        <CarouselContent>
                            {avatars.map((avatar, index) => (
                                <CarouselItem key={index} className="flex aspect-square items-center justify-center ">
                                    <span className="text-9xl">{avatar}</span>
                                </CarouselItem>
                            ))}
                        </CarouselContent>
                        <CarouselPrevious/>
                        <CarouselNext/>
                    </Carousel>
                    <Form {...registerForm}>
                        <form onSubmit={registerForm.handleSubmit(handleRegister)} className="">
                            <FormField
                                control={registerForm.control}
                                name="email"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel className={"mt-4"}>Email</FormLabel>
                                        <FormControl>
                                            <Input type="email" placeholder="mail@mail.com" {...field} />
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={registerForm.control}
                                name="displayName"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Display name</FormLabel>
                                        <FormControl>
                                            <Input placeholder="Bob" {...field} />
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={registerForm.control}
                                name="password"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel className={"mt-4"}>Password</FormLabel>
                                        <FormControl>
                                            <Input type="password" placeholder="password" {...field} />
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />


                            <Button className="mt-4 w-full" type="submit">Register {avatars[selectProfilePic]}</Button>
                        </form>
                    </Form>

                </CardContent>
                <CardFooter className="flex-col gap-2">
                    <div className=" text-center text-m">
                        Already have an account?{" "}
                        <a href="#" onClick={() =>
                            setToggleBetweenRegisterLogin(!toggleBetweenRegisterLogin)
                        } className="underline underline-offset-4">
                            Log in
                        </a>
                    </div>
                </CardFooter>
            </>)}

            {/* ------ LOG IN MODAL ------ */}

            { !toggleBetweenRegisterLogin && (
                <>
                    <CardHeader>
                        <CardTitle className={"text-xl"}>Login to your account</CardTitle>
                        <CardDescription>
                            Enter your email below to login to your account
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <Form {...loginForm}>
                        <form onSubmit={loginForm.handleSubmit(handleLogin)} className="flex flex-col gap-1">
                            <FormField
                                control={loginForm.control}
                                name="email"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel className={"mt-4"}>Email</FormLabel>
                                        <FormControl>
                                            <Input type="email" placeholder="mail@mail.com" {...field} />
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={loginForm.control}
                                name="password"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel htmlFor={"password"} className={"mt-4"}>Password</FormLabel>
                                        <FormControl>
                                            <Input  type="password" placeholder="password" {...field} />
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                                <div className="flex mt-10 flex-col gap-3">
                                    <Button type="button" className="w-full">
                                        Login
                                    </Button>
                                    <Button variant="outline" className="w-full">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                                            <path
                                                d="M12.48 10.92v3.28h7.84c-.24 1.84-.853 3.187-1.787 4.133-1.147 1.147-2.933 2.4-6.053 2.4-4.827 0-8.6-3.893-8.6-8.72s3.773-8.72 8.6-8.72c2.6 0 4.507 1.027 5.907 2.347l2.307-2.307C18.747 1.44 16.133 0 12.48 0 5.867 0 .307 5.387.307 12s5.56 12 12.173 12c3.573 0 6.267-1.173 8.373-3.36 2.16-2.16 2.84-5.213 2.84-7.667 0-.76-.053-1.467-.173-2.053H12.48z"
                                                fill="currentColor"
                                            />
                                        </svg>
                                        Login with Google
                                    </Button>
                                </div>
                            <div className="mt-4 text-center text-m">
                                Don't have an account?{" "}
                                <a href="#" onClick={() =>
                                    setToggleBetweenRegisterLogin(!toggleBetweenRegisterLogin)
                                } className="underline underline-offset-4">
                                   Sign up
                                </a>
                            </div>
                        </form>
                        </Form>
                    </CardContent>
                </>
            )}
        </Card>
    )
}
