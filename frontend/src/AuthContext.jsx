// ─── Auth Context — Smart Firebase + Dev-mode fallback ───────────────────────
import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { healthCheck } from './api';

const AuthContext = createContext(null);

// Check if Firebase env vars are configured
const hasFirebaseConfig = !!(
  import.meta.env.VITE_FIREBASE_API_KEY &&
  import.meta.env.VITE_FIREBASE_API_KEY !== 'AIzaSy...' &&
  import.meta.env.VITE_FIREBASE_PROJECT_ID &&
  import.meta.env.VITE_FIREBASE_PROJECT_ID !== 'your-project-id'
);

// ─── Provider ─────────────────────────────────────────────────────────────────
export function AuthProvider({ children }) {
  const [user, setUser]                 = useState(null);
  const [loading, setLoading]           = useState(true);
  const [backendOnline, setBackendOnline] = useState(null);

  // Backend health check on mount
  useEffect(() => {
    healthCheck()
      .then(() => setBackendOnline(true))
      .catch(() => setBackendOnline(false));
  }, []);

  // Restore session from localStorage on mount
  useEffect(() => {
    const stored = localStorage.getItem('ss_user');
    if (stored) {
      try { setUser(JSON.parse(stored)); } catch {}
    }
    setLoading(false);
  }, []);

  // ─── Internal setter ─────────────────────────────────────────────────────────
  const persistUser = useCallback((userData, token) => {
    localStorage.setItem('ss_user', JSON.stringify(userData));
    localStorage.setItem('auth_token', token);
    setUser(userData);
  }, []);

  // ─── Email/Password Login ─────────────────────────────────────────────────────
  const login = async (email, password) => {
    if (hasFirebaseConfig) {
      const { initializeApp, getApps }          = await import('firebase/app');
      const { getAuth, signInWithEmailAndPassword } = await import('firebase/auth');

      const app  = getApps().length ? getApps()[0] : initializeApp({
        apiKey:            import.meta.env.VITE_FIREBASE_API_KEY,
        authDomain:        import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
        projectId:         import.meta.env.VITE_FIREBASE_PROJECT_ID,
        storageBucket:     import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
        messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
        appId:             import.meta.env.VITE_FIREBASE_APP_ID,
      });
      const auth = getAuth(app);
      const cred = await signInWithEmailAndPassword(auth, email, password);
      const token = await cred.user.getIdToken();
      const userData = {
        uid: cred.user.uid,
        email: cred.user.email,
        displayName: cred.user.displayName || email.split('@')[0],
      };
      persistUser(userData, token);
      return userData;
    } else {
      // ── Demo mode (no Firebase configured) ──
      await new Promise((r) => setTimeout(r, 700));
      const cleanEmail = email.replace(/[^a-zA-Z0-9]/g, '_');
      const token = 'demo_' + cleanEmail;
      const userData = { uid: cleanEmail, email, displayName: email.split('@')[0] };
      persistUser(userData, token);
      return userData;
    }
  };

  // ─── Sign Up ──────────────────────────────────────────────────────────────────
  const signUp = async (email, password) => {
    if (hasFirebaseConfig) {
      const { initializeApp, getApps }               = await import('firebase/app');
      const { getAuth, createUserWithEmailAndPassword } = await import('firebase/auth');
      const app  = getApps().length ? getApps()[0] : initializeApp({
        apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
        authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
        projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
        storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
        messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
        appId: import.meta.env.VITE_FIREBASE_APP_ID,
      });
      const auth = getAuth(app);
      const cred = await createUserWithEmailAndPassword(auth, email, password);
      const token = await cred.user.getIdToken();
      const userData = { uid: cred.user.uid, email: cred.user.email, displayName: email.split('@')[0] };
      persistUser(userData, token);
      return userData;
    } else {
      return login(email, password);
    }
  };

  // ─── Google Sign-In ───────────────────────────────────────────────────────────
  const loginWithGoogle = async () => {
    if (hasFirebaseConfig) {
      const { initializeApp, getApps }               = await import('firebase/app');
      const { getAuth, GoogleAuthProvider, signInWithPopup } = await import('firebase/auth');
      const app      = getApps().length ? getApps()[0] : initializeApp({
        apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
        authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
        projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
        storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
        messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
        appId: import.meta.env.VITE_FIREBASE_APP_ID,
      });
      const auth     = getAuth(app);
      const provider = new GoogleAuthProvider();
      const cred     = await signInWithPopup(auth, provider);
      const token    = await cred.user.getIdToken();
      const userData = { uid: cred.user.uid, email: cred.user.email, displayName: cred.user.displayName || cred.user.email?.split('@')[0] };
      persistUser(userData, token);
      return userData;
    } else {
      await new Promise((r) => setTimeout(r, 500));
      const userData = { uid: 'google_demo', email: 'demo@socialshield.ai', displayName: 'Demo User' };
      persistUser(userData, 'google_demo_' + Date.now());
      return userData;
    }
  };

  // ─── Logout ───────────────────────────────────────────────────────────────────
  const logout = async () => {
    if (hasFirebaseConfig) {
      try {
        const { getApps }  = await import('firebase/app');
        const { getAuth, signOut } = await import('firebase/auth');
        if (getApps().length) await signOut(getAuth(getApps()[0]));
      } catch {}
    }
    localStorage.removeItem('auth_token');
    localStorage.removeItem('ss_user');
    setUser(null);
  };

  // ─── Update User Fields ────────────────────────────────────────────────────────
  const updateUser = async (fields) => {
    if (hasFirebaseConfig) {
      try {
        const { getApps } = await import('firebase/app');
        const { getAuth, updateProfile } = await import('firebase/auth');
        if (getApps().length) {
          const auth = getAuth(getApps()[0]);
          if (auth.currentUser && fields.displayName) {
            await updateProfile(auth.currentUser, { displayName: fields.displayName });
          }
        }
      } catch (err) {
        console.error("Firebase profile update failed:", err);
      }
    }
    setUser((u) => {
      const next = { ...u, ...fields };
      localStorage.setItem('ss_user', JSON.stringify(next));
      return next;
    });
  };

  return (
    <AuthContext.Provider value={{ user, loading, backendOnline, login, signUp, loginWithGoogle, logout, updateUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
