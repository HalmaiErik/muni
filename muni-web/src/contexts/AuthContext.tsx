import { User, UserCredential, createUserWithEmailAndPassword, signInWithEmailAndPassword, signOut } from "firebase/auth";
import React, { ReactNode, useContext, useEffect, useState } from "react";
import { auth } from "../config/firebase";

type Props = {
    children: ReactNode;
};

type AuthContextValue = {
    currentUser: User | null;
    signup: (email: string, password: string) => Promise<UserCredential>;
    login: (email: string, password: string) => Promise<UserCredential>;
    logout: () => Promise<void>;
};

const AuthContext = React.createContext<AuthContextValue | null>(null);

export const useAuth = () => {
    const authContext = useContext(AuthContext);
    if (!authContext) {
        throw new Error("useAuth has to be used within AuthProvider");
    }
    return authContext
}

const LOCAL_STORAGE_TOKEN_KEY = 'idToken';

export const AuthProvider = ({ children }: Props) => {
    const [currentUser, setCurrentUser] = useState<User | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    const signup = (email: string, password: string) => {
        return createUserWithEmailAndPassword(auth, email, password)
    }

    const login = (email: string, password: string) => {
        return signInWithEmailAndPassword(auth, email, password);
    }

    const logout = () => {
        return signOut(auth);
    }

    useEffect(() => {
        const unsubscribe = auth.onAuthStateChanged(user => {
            setCurrentUser(user);
            setLoading(false);
        });

        return unsubscribe;
    }, [])

    useEffect(() => {
        const unsubscribe = auth.onIdTokenChanged(user => {
            if (user) {
                user.getIdToken().then(token => {
                    if (window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY) !== token) {
                        window.localStorage.setItem(LOCAL_STORAGE_TOKEN_KEY, token);
                    }
                });
            }
            else {
                window.localStorage.removeItem(LOCAL_STORAGE_TOKEN_KEY);
            }
        });

        return unsubscribe;
    }, [])

    const value = {
        currentUser,
        signup,
        login,
        logout
    };

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
}