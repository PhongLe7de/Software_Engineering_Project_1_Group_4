import React, {useState} from "react"
import {useAuth} from "@/hooks/useAuth.tsx";
import {zodResolver} from "@hookform/resolvers/zod"
import {useForm} from "react-hook-form"
import {toast} from "sonner"
import {z} from "zod"
import {faker} from '@faker-js/faker';
import {PuffLoader} from "react-spinners";
import {Button} from "@/components/ui/button.tsx"
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle,} from "@/components/ui/card.tsx"
import {
    Carousel,
    type CarouselApi,
    CarouselContent,
    CarouselItem,
    CarouselNext,
    CarouselPrevious,
} from "@/components/ui/carousel.tsx"
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage,} from "@/components/ui/form.tsx"
import {Input} from "@/components/ui/input.tsx"
import { useTranslation } from "react-i18next";
import { LanguageSelector } from "../LanguageSelector";

const RegisterSchema = z.object({
    email: z.email({
        message: "Please enter a valid email address.",
    }),
    displayName: z.string().min(2, {
        message: "Username must be at least 2 characters.",
    }),
    password: z.string().min(5, {
        message: "Password must be at least 5 characters.",
    }),
})

const LoginSchema = z.object({
    email: z.email({
        message: "Please enter a valid email address.",
    }),
    password: z.string().min(5, {
        message: "Password must be at least 5 characters.",
    }),
})

// TODO: Placeholder layout, project part 2: user auth and oAuth 2.0(?)
export default function UserRegisterModal() {
    const [api, setApi] = useState<CarouselApi>()
    const [isLoading, setIsLoading] = useState(false)
    const [selectProfilePic, setSelectProfilePic] = useState(0)
    const [toggleBetweenRegisterLogin, setToggleBetweenRegisterLogin] = useState(true)
    const {login, register} = useAuth();

    const {t} = useTranslation();

    // TODO: Emojis are placeholder. Final ver.: File uploads and default profile pics
    const avatars = ["🦧", "👷"]
    const registerForm = useForm<z.infer<typeof RegisterSchema>>({
        resolver: zodResolver(RegisterSchema),
        defaultValues: {
            email: faker.internet.email(), // TODO: Remove these for prod
            displayName: faker.internet.username(),
            password: "password"
        },
    })
    const loginForm = useForm<z.infer<typeof LoginSchema>>({
        resolver: zodResolver(LoginSchema),
        defaultValues: {
            email: "", // TODO: Remove these for prod
            password: ""
        },
    })

    const handleRegister = async (data: z.infer<typeof RegisterSchema>) => {
        setIsLoading(true)
        try {
            const userData = {
                email: data.email,
                password: data.password,
                displayName: data.displayName,
                photoUrl: avatars[selectProfilePic],
            };
            await register(userData);
            toast.success(`Welcome ${data.displayName}!`);
            setIsLoading(false)
        } catch (e) {
            if (e instanceof TypeError && e.message.includes("NetworkError")) toast.error(e.message);
            console.error("Registration failed:", e);
        } finally {
            setIsLoading(false)
        }
    }

    const handleLogin = async (data: z.infer<typeof LoginSchema>) => {
        setIsLoading(true)
        try {
            const userData: { email: string; password: string } = {
                email: data.email,
                password: data.password,
            }
            const response = await login(userData);
            if (response) {
                toast.success(`${t('auth.welcome_back')} ${response.displayName}!`);
            }
        } catch (e) {
            if (e instanceof TypeError && e.message.includes("NetworkError")) toast.error(e.message);
            console.error("Login failed:", e);
        } finally {
            setIsLoading(false)
        }
    }

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
            <div
                className={"bg-white/30 backdrop-blur-xs absolute top-0 left-0 w-full h-full z-50 flex items-center justify-center" + (isLoading ? " " : " invisible")}>
                <PuffLoader size={80} loading={isLoading} />
            </div>
            {/* ------ REGISTER MODAL ------ */}

            {toggleBetweenRegisterLogin && (
                <>
                    <CardHeader className="flex flex-col text-xl" style={{display: 'flex', flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center'}}>
                        <CardTitle>{t('auth.register')}</CardTitle>
                        <LanguageSelector />
                    </CardHeader>
                    <CardContent>
                        <Carousel className="max-w-xs" setApi={setApi}>
                            <CarouselContent>
                                {avatars.map((avatar, index) => (
                                    <CarouselItem key={index}
                                                  className="flex aspect-square items-center justify-center ">
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
                                            <FormLabel className={"mt-4"}>{t('auth.email')}</FormLabel>
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
                                            <FormLabel>{t('auth.display_name')}</FormLabel>
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
                                            <FormLabel className={"mt-4"}>{t('auth.password')}</FormLabel>
                                            <FormControl>
                                                <Input type="password" placeholder="password" {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />


                                <Button className="mt-4 w-full"
                                        type="submit">{t('auth.register')} {avatars[selectProfilePic]}</Button>
                            </form>
                        </Form>

                    </CardContent>
                    <CardFooter className="flex-col gap-2">
                        <div className=" text-center text-m">
                            {t('auth.already_have_account')}{" "}
                            <a href="#" onClick={() =>
                                setToggleBetweenRegisterLogin(!toggleBetweenRegisterLogin)
                            } className="underline underline-offset-4">
                                {t('auth.login')}
                            </a>
                        </div>
                    </CardFooter>
                </>)}

            {/* ------ LOG IN MODAL ------ */}

            {!toggleBetweenRegisterLogin && (
                <>
                    <CardHeader>
                        <CardTitle className={"text-xl"}>{t('auth.login_to_your_account')}</CardTitle>
                        <LanguageSelector />
                        <CardDescription>
                            {t('auth.enter_email_to_login')}
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
                                            <FormLabel className={"mt-4"}>{t('auth.email')}</FormLabel>
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
                                            <FormLabel htmlFor={"password"} className={"mt-4"}>{t('auth.password')}</FormLabel>
                                            <FormControl>
                                                <Input type="password" placeholder="password" {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <div className="flex mt-10 flex-col gap-3">
                                    <Button type="submit" className="w-full">
                                        {t('auth.login')}
                                    </Button>
                                    <Button variant="outline" className="w-full">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                                            <path
                                                d="M12.48 10.92v3.28h7.84c-.24 1.84-.853 3.187-1.787 4.133-1.147 1.147-2.933 2.4-6.053 2.4-4.827 0-8.6-3.893-8.6-8.72s3.773-8.72 8.6-8.72c2.6 0 4.507 1.027 5.907 2.347l2.307-2.307C18.747 1.44 16.133 0 12.48 0 5.867 0 .307 5.387.307 12s5.56 12 12.173 12c3.573 0 6.267-1.173 8.373-3.36 2.16-2.16 2.84-5.213 2.84-7.667 0-.76-.053-1.467-.173-2.053H12.48z"
                                                fill="currentColor"
                                            />
                                        </svg>
                                        {t('auth.login_with_google')}
                                    </Button>
                                </div>
                                <div className="mt-4 text-center text-m">
                                    {t('auth.dont_have_account')}{" "}
                                    <a href="#" onClick={() =>
                                        setToggleBetweenRegisterLogin(!toggleBetweenRegisterLogin)
                                    } className="underline underline-offset-4">
                                        {t('auth.sign_up')}
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
